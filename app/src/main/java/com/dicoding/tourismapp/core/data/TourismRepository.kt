package com.dicoding.tourismapp.core.data

import com.dicoding.tourismapp.core.data.source.local.LocalDataSource
import com.dicoding.tourismapp.core.data.source.remote.RemoteDataSource
import com.dicoding.tourismapp.core.data.source.remote.network.ApiResponse
import com.dicoding.tourismapp.core.data.source.remote.response.TourismResponse
import com.dicoding.tourismapp.core.domain.model.Tourism
import com.dicoding.tourismapp.core.domain.repository.ITourismRepository
import com.dicoding.tourismapp.core.utils.AppExecutors
import com.dicoding.tourismapp.core.utils.DataMapper
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TourismRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors
) : ITourismRepository {

    override fun getAllTourism(): Flowable<Resource<List<Tourism>>> =
        object : NetworkBoundResource<List<Tourism>, List<TourismResponse>>(appExecutors) {
            override fun loadFromDB(): Flowable<List<Tourism>> {
                return localDataSource.getAllTourism().map {
                    DataMapper.mapEntitiesToDomain(it)
                }
            }

            override fun shouldFetch(data: List<Tourism>?): Boolean =
                data == null || data.isEmpty()

            override fun createCall(): Flowable<ApiResponse<List<TourismResponse>>> =
                remoteDataSource.getAllTourism()

            override fun saveCallResult(data: List<TourismResponse>) {
                val tourismList = DataMapper.mapResponsesToEntities(data)
                localDataSource.insertTourism(tourismList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            }
        }.asFlowable()

    override fun getFavouriteTourism(): Flowable<List<Tourism>> {
        return localDataSource.getFavoriteTourism().map {
            DataMapper.mapEntitiesToDomain(it)
        }
    }

    override fun setFavouriteTourism(tourism: Tourism, state: Boolean) {
        val tourismEntity = DataMapper.mapDomainToEntity(tourism)
        appExecutors.diskIO().execute { localDataSource.setFavoriteTourism(tourismEntity, state) }
    }
}

