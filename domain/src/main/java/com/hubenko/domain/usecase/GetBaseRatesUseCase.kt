package com.hubenko.domain.usecase

import com.hubenko.domain.model.BaseRate
import com.hubenko.domain.repository.BaseRateDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBaseRatesUseCase @Inject constructor(
    private val repository: BaseRateDataSource
) {
    operator fun invoke(): Flow<List<BaseRate>> = repository.getBaseRates()
}
