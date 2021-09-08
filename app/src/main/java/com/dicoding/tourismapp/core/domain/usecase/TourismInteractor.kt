package com.dicoding.tourismapp.core.domain.usecase

import com.dicoding.tourismapp.core.domain.model.Tourism
import com.dicoding.tourismapp.core.domain.repository.ITourismRepository

class TourismInteractor(private val tourismRepository: ITourismRepository) : TourismUseCase {
    override fun getAllTourism() = tourismRepository.getAllTourism()

    override fun getFavouriteTourism() = tourismRepository.getFavouriteTourism()

    override fun setFavouriteTourism(tourism: Tourism, state: Boolean) =
        tourismRepository.setFavouriteTourism(tourism, state)
}