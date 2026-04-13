package com.hubenko.domain.usecase

import com.hubenko.domain.model.HourlyRate
import com.hubenko.domain.repository.HourlyRateDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHourlyRatesUseCase @Inject constructor(
    private val repository: HourlyRateDataSource
) {
    operator fun invoke(): Flow<List<HourlyRate>> = repository.getHourlyRates()
}
