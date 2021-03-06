package cn.onlyloveyd.wanandroidclient.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import cn.onlyloveyd.wanandroidclient.R
import cn.onlyloveyd.wanandroidclient.bean.HttpResult
import cn.onlyloveyd.wanandroidclient.bean.LoginData
import cn.onlyloveyd.wanandroidclient.ext.Ext
import cn.onlyloveyd.wanandroidclient.ext.Preference
import cn.onlyloveyd.wanandroidclient.http.Retrofitance
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast

/**
 * 文 件 名: LoginFragment
 * 创建日期: 2018/2/9 18:45
 * 邮   箱: yidong@gz.csg.cn
 * 描   述：
 * @author Mraz
 */
class LoginActivity : AppCompatActivity() {
    private val progressDialog by lazy {
        ProgressDialog(this@LoginActivity,
                R.style.AppTheme_Dark_Dialog)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_layout.visibility = View.VISIBLE
        singup_layout.visibility = View.GONE
        link_signup.setOnClickListener { _ ->
            login_layout.visibility = View.GONE
            singup_layout.visibility = View.VISIBLE
        }

        link_login.setOnClickListener { _ ->
            login_layout.visibility = View.VISIBLE
            singup_layout.visibility = View.GONE
        }

        toolbar.setNavigationOnClickListener { _ ->
            finish()
        }

        btn_login.setOnClickListener { _ -> login() }

        btn_signup.setOnClickListener { _ -> signUp() }

    }

    private fun signUp() {
        if (!validateSignUp()) {
            onSignupFailed("注册失败")
            return
        }
        btn_login.isEnabled = false

        this.progressDialog.let {
            progressDialog.isIndeterminate = true
            progressDialog.setMessage("注册中...")
            progressDialog.setCancelable(false)
            it.show()
        }

        val username = signup_input_name.text.toString()
        val password = signup_input_password.text.toString()
        val reEnterPassword = signup_input_reEnterPassword.text.toString()

        // TODO: Implement your own authentication logic here.
        Retrofitance.wanAndroidAPI.registerWanAndroid(username, password, reEnterPassword).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it: HttpResult<LoginData> ->
                    if (it.errorCode == 0) {
                        onSignupSuccess(it.data)
                    } else {
                        onSignupFailed(it.errorMsg)
                    }
                }, { it: Throwable ->
                    it.printStackTrace()
                })
    }

    private fun onSignupSuccess(signupData: LoginData) {

        btn_signup.isEnabled = true

        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }

        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
        var status: Boolean by Preference(Ext.PREF_IS_LOGIN, false)
        @Suppress("UNUSED_VALUE")
        status = true

        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
        var username: String by Preference(Ext.PREF_USERNAME, "")
        @Suppress("UNUSED_VALUE")
        username = signupData.username

        val data = Intent()
        data.putExtra("username", signupData.username)
        data.putExtra("password", signupData.password)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun onSignupFailed(message: String) {
        toast(message)
        btn_signup.isEnabled = true
    }

    private fun validateSignUp(): Boolean {
        var valid = true

        val name = signup_input_name.text.toString()
        val password = signup_input_password.text.toString()
        val reEnterPassword = signup_input_reEnterPassword.text.toString()

        if (name.isEmpty()) {
            til_signup_name.error = "用户名不能为空"
            valid = false
        } else {
            til_signup_name.error = null
        }

        if (password.isEmpty()) {
            til_signup_password.error = "密码不能为空"
            valid = false
        } else {
            til_signup_password.error = null
        }

        if (reEnterPassword.isEmpty() || reEnterPassword != password) {
            til_signup_reEnterPassword.error = "两次输入的密码不一致"
            valid = false
        } else {
            til_signup_reEnterPassword.error = null
        }

        return valid
    }

    private fun login() {
        if (!validate()) {
            onLoginFailed("不合法的用户名或者密码")
            return
        }

        btn_login.isEnabled = false

        this.progressDialog.let {
            progressDialog.isIndeterminate = true
            progressDialog.setMessage("登录中...")
            progressDialog.setCancelable(false)
            it.show()
        }

        val username = login_input_username.text.toString()
        val password = login_input_password.text.toString()

        // TODO: Implement your own authentication logic here.
        Retrofitance.wanAndroidAPI.loginWanAndroid(username, password).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it: HttpResult<LoginData> ->
                    if (it.errorCode == 0) {
                        onLoginSuccess(it.data)
                    } else {
                        onLoginFailed(it.errorMsg)
                    }
                }, { it: Throwable ->
                    it.printStackTrace()
                })
    }

    private fun validate(): Boolean {
        var valid = true

        val username = login_input_username.text.toString()
        val password = login_input_password.text.toString()

        if (username.isEmpty()) {
            login_username_til.error = "请输入用户名"
            valid = false
        } else {
            login_username_til.error = null
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            login_password_til.error = "密码长度4-10位"
            valid = false
        } else {
            login_password_til.error = null
        }
        return valid
    }


    private fun onLoginFailed(message: String) {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
        longToast(message)
        btn_login.isEnabled = true
    }

    private fun onLoginSuccess(loginData: LoginData) {
        btn_login.isEnabled = true
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }

        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
        var status: Boolean by Preference(Ext.PREF_IS_LOGIN, false)
        @Suppress("UNUSED_VALUE")
        status = true

        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
        var username: String by Preference(Ext.PREF_USERNAME, "")
        @Suppress("UNUSED_VALUE")
        username = loginData.username

        val data = Intent()
        data.putExtra("username", loginData.username)
        data.putExtra("password", loginData.password)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onPause() {
        super.onPause()
        this.progressDialog.let {
            if (this.isFinishing) {
                this.progressDialog.dismiss()
            }
        }
    }

}