package com.inncreator.testpoints.features.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.inncreator.testpoints.R
import com.inncreator.testpoints.data.models.Point
import com.inncreator.testpoints.domain.models.ProcessedPoint
import com.inncreator.testpoints.features.main.common.UiEffect
import com.inncreator.testpoints.features.main.common.UiError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputCount = view.findViewById<EditText>(R.id.input_count)
        val buttonStart = view.findViewById<Button>(R.id.button_start)

        buttonStart.setOnClickListener {
            val count = inputCount.text.toString().toIntOrNull()
            if (count == null || count <= 0) {
                showErrorDialog(getString(R.string.error_main_invalid_count))
                return@setOnClickListener
            }
            viewModel.fetchPoints(count)
        }
        observeUiEffect()
    }

    private fun observeUiEffect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiEffect.collectLatest { effect ->
                when (effect) {
                    is UiEffect.Success -> navigateToPointsFragment(effect.data)
                    is UiEffect.Error -> showErrorDialog(getErrorMessage(effect.error))
                }
            }
        }
    }

    private fun navigateToPointsFragment(points: List<ProcessedPoint>) {
        val action =
            MainFragmentDirections.actionMainFragmentToPointsFragment(points.toTypedArray())
        findNavController().navigate(action)
    }

    private fun getErrorMessage(error: UiError): String {
        return when (error) {
            is UiError.ServerError -> getString(
                R.string.error_server,
                error.code,
                error.message ?: ""
            )

            is UiError.NetworkError -> getString(R.string.error_network)
            is UiError.UnknownError -> getString(R.string.error_unknown)
        }
    }

    private fun showErrorDialog(message: String) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.error))
            .setMessage(message)
            .setNegativeButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}