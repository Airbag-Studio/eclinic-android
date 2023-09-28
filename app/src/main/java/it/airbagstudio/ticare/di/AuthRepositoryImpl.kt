package it.airbagstudio.ticare.di

import android.content.Context
import ch.ticare.eclinic.library.network.AuthRepository

class AuthRepositoryImpl(context: Context): AuthRepository {
    private val sharedPreferences = context.getSharedPreferences("ticareshared",Context.MODE_PRIVATE)
    private val companyGroupKey = "COMPANY_GROUP_KEY"
    private val companyNameKey = "COMPANY_NAME_KEY"
    private val refreshTokenKey = "REFRESH_TOKEN_KEY"
    private val tokenKey = "TOKEN_KEY"
    private val uuidKey = "UUID_KEY"
    private val baseUrlKey = "BASE_URL_KEY"

    override fun getBaseURL(): String {
        return sharedPreferences.getString(baseUrlKey,"") ?: ""
    }

    override fun getCompanyGroup(): String? {
        return sharedPreferences.getString(companyGroupKey,null)
    }

    override fun getCompanyName(): String? {
        return sharedPreferences.getString(companyNameKey,null)
    }

    override fun getRefreshToken(): String? {
        return sharedPreferences.getString(refreshTokenKey,null)
    }

    override fun getToken(): String? {
        return sharedPreferences.getString(tokenKey,null)
    }

    override fun getUUID(): String? {
        return  "8b827876-f089-4153-a8b5-006e63bfc957"
    //return sharedPreferences.getString(uuidKey,null)
    }

    override fun setBaseURL(url: String) {
        sharedPreferences.edit().putString(baseUrlKey,url).apply()
    }

    override fun setCompanyGroup(group: String?) {
        sharedPreferences.edit().putString(companyGroupKey,group).apply()
    }

    override fun setCompanyName(name: String?) {
        sharedPreferences.edit().putString(companyNameKey,name).apply()
    }

    override fun setRefreshToken(token: String?) {
        sharedPreferences.edit().putString(refreshTokenKey,token).commit()
    }

    override fun setToken(token: String?) {
        sharedPreferences.edit().putString(tokenKey,token).commit()
    }

    override fun setUUID(uuid: String?) {
        sharedPreferences.edit().putString(uuidKey,uuid).apply()
    }

}