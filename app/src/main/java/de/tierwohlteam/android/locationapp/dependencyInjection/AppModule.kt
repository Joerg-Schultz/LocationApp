package de.tierwohlteam.android.locationapp.dependencyInjection

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.tierwohlteam.android.locationapp.others.Constants.LOCATIONAPP_DB_NAME
import de.tierwohlteam.android.locationapp.repositories.LocationAppDB
import de.tierwohlteam.android.locationapp.repositories.LocationAppRepository

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFutterAppDB(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app.applicationContext,
        LocationAppDB::class.java,
        LOCATIONAPP_DB_NAME
    ) //.allowMainThreadQueries() //devdebug only!!!!
        //.fallbackToDestructiveMigration() // comment out in production
        .build()

    @Singleton
    @Provides
    fun provideRepository(db: LocationAppDB) = LocationAppRepository(db)

}