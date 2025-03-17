package it.airbagstudio.ticare.di

import android.content.Context
import ch.ticare.eclinic.library.network.AuthRepository
import it.airbagstudio.ticare.BuildConfig

class AuthRepositoryImpl(context: Context): AuthRepository {
    private val sharedPreferences = context.getSharedPreferences("ticareshared",Context.MODE_PRIVATE)
    private val companyGroupKey = "COMPANY_GROUP_KEY"
    private val companyNameKey = "COMPANY_NAME_KEY"
    private val refreshTokenKey = "REFRESH_TOKEN_KEY"
    private val tokenKey = "TOKEN_KEY"
    private val uuidKey = "UUID_KEY"
    private val baseUrlKey = "BASE_URL_KEY"
    private val usernameKey = "USERNAME_KEY"
    private val passwordKey = "PASSWORD_KEY"
    private val rememberKey = "REMEMBER_KEY"
    private val userValueKey = "USER_VALUE_KEY"

    override fun getBaseURL(): String {
        return sharedPreferences.getString(baseUrlKey,"")?.replace("/v1","") ?: ""
    }

    override fun getCompanyGroup(): String? {
        return sharedPreferences.getString(companyGroupKey,null)
    }

    override fun getCompanyName(): String? {
        return sharedPreferences.getString(companyNameKey,null)
    }

    override fun getPassword(): String? {
        return sharedPreferences.getString(passwordKey,null)
    }

    override fun getRefreshToken(): String? {
        return sharedPreferences.getString(refreshTokenKey,null)
    }

    override fun getToken(): String? {
        return sharedPreferences.getString(tokenKey,null)
    }

    override fun getUUID(): String? {
        val savedUUid = sharedPreferences.getString(uuidKey,null)
        if (!savedUUid.isNullOrEmpty()){
            return savedUUid
        }
        val newUUID = java.util.UUID.randomUUID().toString()
        sharedPreferences.edit().putString(uuidKey,newUUID).commit()
        return newUUID
    }

    override fun getUserValue(): String? {
        return sharedPreferences.getString(userValueKey,null)
    }

    override fun getUsername(): String? {
        return sharedPreferences.getString(usernameKey,null)
    }

    override fun getRememberMe(): Boolean{
        return sharedPreferences.getBoolean(rememberKey,false)
    }

    override fun getAppVersion(): String {
        return BuildConfig.VERSION_NAME
    }

    override fun setBaseURL(url: String) {
        sharedPreferences.edit().putString(baseUrlKey,url).commit()
    }

    override fun setCompanyGroup(group: String?) {
        sharedPreferences.edit().putString(companyGroupKey,group).commit()
    }

    override fun setCompanyName(name: String?) {
        sharedPreferences.edit().putString(companyNameKey,name).commit()
    }

    override fun setPassword(password: String?) {
        sharedPreferences.edit().putString(passwordKey,password).commit()
    }

    override fun setRefreshToken(token: String?) {
        sharedPreferences.edit().putString(refreshTokenKey,token).commit()
    }

    override fun setToken(token: String?) {
        sharedPreferences.edit().putString(tokenKey,token).commit()
    }

    override fun setUUID(uuid: String?) {
        sharedPreferences.edit().putString(uuidKey,uuid).commit()
    }

    override fun setUserValue(userValue: String?) {
        sharedPreferences.edit().putString(userValueKey,userValue).commit()
    }

    override fun setUsername(username: String?) {
        sharedPreferences.edit().putString(usernameKey,username).commit()
    }

    override fun setRememberMe(value:Boolean){
        sharedPreferences.edit().putBoolean(rememberKey,value).commit()
    }
}