package com.nuecoo.core.data.datasource.local

import com.nuecoo.core.data.database.dao.UserInfoDao
import com.nuecoo.core.data.model.local.LocalUserInfo
import javax.inject.Inject

class UserInfoDataSourceImpl @Inject constructor(
    private val userInfoDao: UserInfoDao
) : UserInfoDataSource {
    override suspend fun upsertUserInfo(entity: LocalUserInfo) = userInfoDao.upsertUserInfo(entity)
    override suspend fun getUserInfo() = userInfoDao.getUserInfo()
    override suspend fun deleteUserInfo() = userInfoDao.deleteUser()
    override fun getUserInfoFlow() = userInfoDao.getUserInfoFlow()
}