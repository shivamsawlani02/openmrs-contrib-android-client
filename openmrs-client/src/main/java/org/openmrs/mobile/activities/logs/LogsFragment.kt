/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.mobile.activities.logs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import org.openmrs.mobile.R
import org.openmrs.mobile.databinding.FragmentLogsBinding

class LogsFragment : Fragment() {

    private var _binding: FragmentLogsBinding? = null
    private val binding get() = _binding!!
    private val mViewModel by viewModels<LogsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentLogsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachLogsToTextView()

        binding.fab.setOnClickListener {
            fabCopyAll()
        }
    }

    private fun attachLogsToTextView() {
        mViewModel.getLogs()
        mViewModel.logsText.observe(viewLifecycleOwner, Observer { updatedLogTexts ->
            binding.tvLogs.text = updatedLogTexts
        })
    }

    private fun fabCopyAll() {
        mViewModel.getLogs()
        mViewModel.logsText.observe(viewLifecycleOwner, Observer { updatedLogTexts ->
            setClipboard(context, updatedLogTexts)
            Toast.makeText(context, R.string.logs_copied_to_clipboard_message,
                    Toast.LENGTH_SHORT).show()
        })
    }

    private fun setClipboard(context: Context?, text: String?) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(getString(R.string.copied_text), text)
        clipboard.setPrimaryClip(clip)
    }

    companion object {
        fun newInstance(): LogsFragment {
            return LogsFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}