package com.rejowan.numberconverter.di

import com.rejowan.numberconverter.repository.ConverterRepository
import com.rejowan.numberconverter.repositoryImpl.ConverterRepositoryImpl
import com.rejowan.numberconverter.viewmodel.ConverterViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val converterModule = module {
    single<ConverterRepository> { ConverterRepositoryImpl(androidContext()) }
    viewModel { ConverterViewModel(get())}
}