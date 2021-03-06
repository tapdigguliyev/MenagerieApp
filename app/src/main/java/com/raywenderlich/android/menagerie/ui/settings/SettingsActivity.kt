package com.raywenderlich.android.menagerie.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.view.doOnLayout
import com.raywenderlich.android.menagerie.databinding.ActivitySettingsBinding
import com.raywenderlich.android.menagerie.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity(), SettingsView {

  private val binding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
  private val settingsViewModel by viewModels<SettingsViewModel>()

  companion object {
    fun getIntent(context: Context) = Intent(context, SettingsActivity::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    settingsViewModel.setView(this)
    overridePendingTransition(0,0)
    setContentView(binding.root)
    setupUi()
    // Todo enter animation
    setupCircularReveal(savedInstanceState)
  }

  private fun setupCircularReveal(savedInstanceState: Bundle?) {
    if (savedInstanceState == null) {
      binding.settingsRoot.visibility = View.INVISIBLE

      binding.settingsRoot.doOnLayout {
        revealCircular()
      }
    }
  }

  private fun revealCircular() {
    val rootHeight = binding.settingsRoot.height
    val centerX = binding.settingsButton.x
    val centerY = binding.settingsButton.y / 2

    val circularReveal = ViewAnimationUtils.createCircularReveal(
      binding.settingsRoot,
      centerX.toInt(),
      centerY.toInt(),
      0f,
      rootHeight * 2f
    )

    circularReveal.duration = 1000

    binding.settingsRoot.visibility = View.VISIBLE

    circularReveal.start()
  }

  private fun setupUi() {
    binding.settingsButton.setOnClickListener { exitCircular() }
    binding.sleepAllPets.setOnClickListener { settingsViewModel.putAllPetsToBed() }
    binding.wakeAllPets.setOnClickListener { settingsViewModel.wakeUpAllPets() }
    binding.logOut.setOnClickListener { settingsViewModel.logOut() }

    settingsViewModel.sleepingPets.observe(this, { sleepingPets ->
      if (sleepingPets != null && sleepingPets.isNotEmpty()) {
        val allPetsSleeping = sleepingPets.all { it.isSleeping }
        val isAnyPetAsleep = sleepingPets.any { it.isSleeping }

        binding.sleepAllPets.isEnabled = !allPetsSleeping
        binding.wakeAllPets.isEnabled = isAnyPetAsleep
      } else {
        binding.sleepAllPets.isEnabled = true
        binding.wakeAllPets.isEnabled = false
      }
    })
  }

  override fun onUserLoggedOut() {
    // TODO transition
    startActivity(LoginActivity.getIntent(this))
  }

  override fun onBackPressed() = exitCircular()

  private fun exitCircular() {
    // TODO animation
    val rootHeight = binding.settingsRoot.height
    val centerX = binding.settingsButton.x
    val centerY = binding.settingsButton.y / 2

    val circularReveal = ViewAnimationUtils.createCircularReveal(
      binding.settingsRoot,
      centerX.toInt(),
      centerY.toInt(),
      rootHeight * 1.2f,
      0f
    )

    circularReveal.duration = 1000

    circularReveal.doOnEnd {
      finish()
      binding.settingsRoot.visibility = View.INVISIBLE
      overridePendingTransition(0, 0)
    }

    circularReveal.start()
  }
}