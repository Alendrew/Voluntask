package com.example.voluntask.ui

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.voluntask.R
import com.example.voluntask.databinding.FragmentLoginBinding
import com.example.voluntask.models.enums.TipoConta
import com.example.voluntask.util.CustomToast
import com.example.voluntask.util.LoadingUI
import com.example.voluntask.util.Types
import com.example.voluntask.viewmodels.AuthViewModel


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        val customToast = CustomToast(binding.customToast, requireContext())
        var loadingUI: LoadingUI

        binding.btnLogin.setOnClickListener {

            val email = binding.inputEmail.text.toString()
            val senha = binding.inputSenha.text.toString()

            if (
                email.isBlank() ||
                senha.isBlank()
            ) {
                customToast.showCustomToast(
                    "Todos os campos precisam ser preenchidos",
                    Types.WARNING
                )
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString())
                    .matches()
            ) {
                customToast.showCustomToast("E-mail inválido", Types.ERROR)
            } else {

                loadingUI = LoadingUI(binding.btnLogin,binding.progressCircular, listOf<View>(binding.btnRegister,binding.btnForgot))
                loadingUI.btnToLoading()
                viewModel.login(email, senha) { resultado ->
                    if (resultado.result) {
                        viewModel.getUserInfo(resultado.user!!.uid.toString()) { usuario ->
                            val bundle = bundleOf("usuario" to usuario)
                            Navigation.findNavController(binding.root)
                                .navigate(R.id.action_loginFragment_to_homeFragment, bundle)
                        }
                    } else {
                        customToast.showCustomToast(resultado.msg, Types.ERROR)
                        loadingUI.loadingToBtn()
                    }
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_loginFragment_to_escolherContaFragment)
        }

        binding.btnForgot.setOnClickListener {
            if (binding.inputEmail.text.toString().isBlank()) {
                customToast.showCustomToast("Insira um E-mail", Types.WARNING)
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString())
                    .matches()
            ) {
                customToast.showCustomToast("E-mail inválido", Types.ERROR)
            } else {
                val email = binding.inputEmail.text.toString()

                loadingUI = LoadingUI(binding.btnForgot, binding.loading, null)
                loadingUI.btnToLoading()
                viewModel.forget(email) {
                    if (it.result) {
                        customToast.showCustomToast(
                            "E-mail de recuperação enviado com sucesso, verifique seu e-mail", Types.SUCESS)
                        loadingUI.loadingToBtn()
                    } else {
                        customToast.showCustomToast(it.msg, Types.ERROR)
                    }
                    loadingUI.loadingToBtn()
                }
            }
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}