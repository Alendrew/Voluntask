package com.example.voluntask.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.voluntask.R
import com.example.voluntask.databinding.FragmentRegisterInstituicaoBinding
import com.example.voluntask.models.Instituicao
import com.example.voluntask.models.enums.TipoConta
import com.example.voluntask.util.CustomToast
import com.example.voluntask.util.LoadingUI
import com.example.voluntask.util.Types
import com.example.voluntask.viewmodels.AuthViewModel
import java.time.Instant
import java.util.Date


class RegisterInstituicaoFragment : Fragment() {

    private lateinit var binding: FragmentRegisterInstituicaoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterInstituicaoBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        val customToast = CustomToast(binding.customToast, requireContext())
        var loadingUI: LoadingUI

        binding.btnRegister.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            val senha = binding.inputSenha.text.toString()
            val nome = binding.inputNome.text.toString()
            val telefone = binding.inputTelefone.unMasked
            val cnpj = binding.inputCnpj.unMasked
            val nomeResp = binding.inputNomeResp.text.toString()
            val cpfResp = binding.inputCpfResp.unMasked
            val confirmarSenha = binding.inputConfirmarSenha.text.toString()
            val dataCadastro = Date.from(Instant.now())
            val tipoConta = TipoConta.INSTITUICAO

            if (
                nome.isBlank() ||
                email.isBlank() ||
                telefone.isBlank() ||
                cnpj.isBlank() ||
                nomeResp.isBlank() ||
                cpfResp.isBlank() ||
                senha.isBlank() ||
                confirmarSenha.isBlank()
            ) {
                customToast.showCustomToast("Todos os campos precisam ser preenchidos", Types.WARNING)
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString())
                    .matches()
            ) {
                customToast.showCustomToast("E-mail inválido",Types.ERROR)
            } else if (senha != confirmarSenha) {
                customToast.showCustomToast("As senhas não conferem",Types.ERROR)
            } else if (senha.length < 6) {
                customToast.showCustomToast("Senha menor que 6 caracteres",Types.ERROR)
            } else if (!binding.inputCpfResp.isDone) {
                customToast.showCustomToast("Cpf inválido",Types.ERROR)
            } else if (!binding.inputCnpj.isDone) {
                customToast.showCustomToast("Cnpj inválido",Types.ERROR)
            } else if (!binding.inputTelefone.isDone) {
                customToast.showCustomToast("Número de telefone inválido",Types.ERROR)
            } else {

                val instituicao =
                    Instituicao(nome, nomeResp, telefone, tipoConta, cnpj, cpfResp, dataCadastro)

                loadingUI = LoadingUI(binding.btnRegister,binding.progressCircular,null)
                loadingUI.btnToLoading()
                viewModel.register(instituicao, email, senha) {
                    if (it.result) {
                        val delayMillis = 2000L // 2 segundos

                        // Use um Handler para ocultar a Toast após o atraso
                        Handler(Looper.getMainLooper()).postDelayed({
                            Navigation.findNavController(binding.root)
                                .navigate(R.id.action_registerInstituicaoFragment_to_loginFragment)
                        }, delayMillis)
                    } else {
                        // O registro não foi bem-sucedido
                        loadingUI.loadingToBtn()
                        customToast.showCustomToast(it.msg,Types.ERROR)
                    }
                }
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterVoluntarioFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
