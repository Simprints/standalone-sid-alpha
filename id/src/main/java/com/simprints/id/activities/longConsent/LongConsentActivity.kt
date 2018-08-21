package com.simprints.id.activities.longConsent

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.simprints.id.Application
import com.simprints.id.R
import com.simprints.id.data.prefs.PreferencesManager
import com.simprints.id.tools.LanguageHelper
import kotlinx.android.synthetic.main.activity_long_consent.*
import javax.inject.Inject

class LongConsentActivity : AppCompatActivity(), LongConsentContract.View {

    @Inject lateinit var preferences: PreferencesManager

    override lateinit var viewPresenter: LongConsentContract.Presenter

    override var showProgressBar: Boolean = true
        set(value) {
            field = value
            longConsent_progressBar.visibility = if (value) View.VISIBLE else View.INVISIBLE
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val component = (application as Application).component.also { it.inject(this) }
        LanguageHelper.setLanguage(this, preferences.language)
        setContentView(R.layout.activity_long_consent)

        initActionBar()

        viewPresenter = LongConsentPresenter(this, component)
        viewPresenter.start()
    }

    private fun initActionBar() {
        setSupportActionBar(longConsentToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setTitle(R.string.privacy_notice_title)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun setLongConsentText(text: String) {
        longConsent_TextView.text = text
        longConsent_ScrollView.requestLayout()
    }

    override fun setDefaultLongConsent() = setLongConsentText(getString(R.string.long_consent))
}