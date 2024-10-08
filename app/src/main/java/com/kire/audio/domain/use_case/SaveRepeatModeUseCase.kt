package com.kire.audio.domain.use_case

import com.kire.audio.device.audio.util.RepeatMode
import com.kire.audio.domain.repository.IPreferencesRepository
import com.kire.audio.domain.constants.PreferencesDataStoreConstants
import javax.inject.Inject

class SaveRepeatModeUseCase @Inject constructor(
    private val preferencesDataStoreRepository: IPreferencesRepository
) {

    suspend operator fun invoke(value: Int) =
        preferencesDataStoreRepository
            .saveRepeatMode(
                PreferencesDataStoreConstants.REPEAT_MODE_KEY,
                RepeatMode.entries[value].name
            )
}
