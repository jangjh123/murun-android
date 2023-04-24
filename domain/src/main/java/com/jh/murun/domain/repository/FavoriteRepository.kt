package com.jh.murun.domain.repository

import com.jh.murun.domain.model.Music
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun readAllMusics(): Flow<List<Music>?>
    suspend fun getMusicById(id: String): Flow<Music?>
    suspend fun insertMusicToFavoriteList(music: Music): Flow<Boolean>
    suspend fun deleteMusicFromFavoriteList(music: Music): Flow<Boolean>
}