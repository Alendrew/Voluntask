package com.example.voluntask.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.voluntask.R
import com.example.voluntask.databinding.FragmentRegisterInstituicaoBinding
import com.example.voluntask.models.Instituicao
import com.example.voluntask.models.Usuario
import com.example.voluntask.models.Voluntario
import com.example.voluntask.models.enums.Generos
import com.example.voluntask.models.enums.TipoConta
import com.example.voluntask.util.CustomToast
import com.example.voluntask.util.LoadingUI
import com.example.voluntask.util.Types
import com.example.voluntask.viewmodels.AuthViewModel
import com.example.voluntask.viewmodels.SharedViewModel
import com.santalu.maskara.Mask
import com.santalu.maskara.MaskChangedListener
import com.santalu.maskara.MaskStyle
import java.time.Instant
import java.time.LocalDate
import java.util.Date


class RegisterInstituicaoFragment : Fragment() {

    private lateinit var binding: FragmentRegisterInstituicaoBinding
    private lateinit var sharedViewModel: SharedViewModel

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
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        val usuario = arguments?.getParcelable<Usuario>("usuario") as Instituicao?
        var loadingUI: LoadingUI


        if (usuario != null) {
            binding.h1.text = "Editar Perfil"
            binding.btnRegister.text = "Salvar"
            binding.btnExcluirPerfil.visibility = View.VISIBLE
            binding.steps.visibility = View.GONE
            val telefoneMask = Mask(
                value = "+55 (XX) XXXXXXXXX",
                character = 'X',
                style = MaskStyle.NORMAL
            )
            val cpfMask = Mask(
                value = "XXX.XXX.XXX-XX",
                character = 'X',
                style = MaskStyle.NORMAL
            )
            val cnpjMask = Mask(
                value = "XX.XXX.XXX/XXXX-XX",
                character = 'X',
                style = MaskStyle.NORMAL
            )
            binding.inputTelefone.addTextChangedListener(MaskChangedListener(telefoneMask))
            binding.inputCpfResp.addTextChangedListener(MaskChangedListener(cpfMask))
            binding.inputCnpj.addTextChangedListener(MaskChangedListener(cnpjMask))
            binding.inputTelefone.setText(usuario.telefone)
            binding.inputCpfResp.setText(usuario.cpfRepresentanteLegal)
            binding.inputCnpj.setText(usuario.cnpj)
            binding.inputEmail.setText(usuario.email)
            binding.topSpace.layoutParams.height = 250
            binding.inputSenhaLayout.visibility = View.GONE
            binding.inputConfirmarSenhaLayout.visibility = View.GONE
            binding.inputEmailLayout.isEnabled = false
            binding.inputEmail.isEnabled = false
            binding.inputNome.setText(usuario.nome)
            binding.inputNomeResp.setText(usuario.nomeRepresentanteLegal)
        } else {
            binding.inputEmail.setCompoundDrawables(null, null, null, null)
        }
        binding.h1.visibility = View.VISIBLE
        binding.btnRegister.visibility = View.VISIBLE

        binding.btnExcluirPerfil.setOnClickListener {
            val bundle = bundleOf("usuario" to usuario)
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_registerInstituicaoFragment_to_excluirPerfilFragment, bundle)
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            val senha = binding.inputSenha.text.toString()
            val nome = binding.inputNome.text.toString()
            val telefone = binding.inputTelefone.unMasked
            val cnpj = binding.inputCnpj.unMasked
            val nomeResp = binding.inputNomeResp.text.toString()
            val cpfResp = binding.inputCpfResp.unMasked
            val confirmarSenha = binding.inputConfirmarSenha.text.toString()
            val dataCadastro = LocalDate.now()
            val tipoConta = TipoConta.INSTITUICAO

            if (usuario == null) {
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
                    customToast.showCustomToast(
                        "Todos os campos precisam ser preenchidos",
                        Types.WARNING
                    )
                } else if (senha != confirmarSenha) {
                    customToast.showCustomToast("As senhas não conferem", Types.ERROR)
                } else if (senha.length < 6) {
                    customToast.showCustomToast("Senha menor que 6 caracteres", Types.ERROR)
                } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString())
                        .matches()
                ) {
                    customToast.showCustomToast("E-mail inválido", Types.ERROR)
                } else if (!binding.inputCpfResp.isDone) {
                    customToast.showCustomToast("Cpf inválido", Types.ERROR)
                } else if (!binding.inputCnpj.isDone) {
                    customToast.showCustomToast("Cnpj inválido", Types.ERROR)
                } else if (!binding.inputTelefone.isDone) {
                    customToast.showCustomToast("Número de telefone inválido", Types.ERROR)
                } else {
                    val newInstituicao =
                        Instituicao(
                            nome,
                            nomeResp,
                            telefone,
                            tipoConta,
                            cnpj,
                            "",
                            cpfResp,
                            "${dataCadastro.year}-${dataCadastro.month.value}-${dataCadastro.dayOfMonth}"
                        )
                    loadingUI = LoadingUI(binding.btnRegister, binding.progressCircular, null)
                    loadingUI.btnToLoading()
                    viewModel.register(newInstituicao, email, senha) {
                        if (it.result) {
                            customToast.showCustomToast(it.msg, Types.SUCESS)
                            val delayMillis = 2000L // 2 segundos

                            // Use um Handler para ocultar a Toast após o atraso
                            Handler(Looper.getMainLooper()).postDelayed({
                                Navigation.findNavController(binding.root)
                                    .navigate(R.id.action_registerInstituicaoFragment_to_loginFragment)
                            }, delayMillis)
                        } else {
                            // O registro não foi bem-sucedido
                            loadingUI.loadingToBtn()
                            customToast.showCustomToast(it.msg, Types.ERROR)
                        }
                    }
                }
            } else {
                if (
                    nome.isBlank() ||
                    email.isBlank() ||
                    telefone.isBlank() ||
                    cnpj.isBlank() ||
                    nomeResp.isBlank() ||
                    cpfResp.isBlank()
                ) {
                    customToast.showCustomToast(
                        "Todos os campos precisam ser preenchidos",
                        Types.WARNING
                    )
                } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString())
                        .matches()
                ) {
                    customToast.showCustomToast("E-mail inválido", Types.ERROR)
                } else if (!binding.inputCpfResp.isDone) {
                    customToast.showCustomToast("Cpf inválido", Types.ERROR)
                } else if (!binding.inputCnpj.isDone) {
                    customToast.showCustomToast("Cnpj inválido", Types.ERROR)
                } else if (!binding.inputTelefone.isDone) {
                    customToast.showCustomToast(
                        "Número de telefone inválido",
                        Types.ERROR
                    )
                }
                val newInstituicao =
                    Instituicao(
                        nome,
                        nomeResp,
                        telefone,
                        tipoConta,
                        cnpj,
                        "",
                        cpfResp,
                        "${dataCadastro.year}-${dataCadastro.month.value}-${dataCadastro.dayOfMonth}"
                    )
                newInstituicao.idUsuario = usuario!!.idUsuario
                newInstituicao.dataCadastro = usuario.dataCadastro
                newInstituicao.email = usuario.email
                newInstituicao.idInfoConta = usuario.idInfoConta
                newInstituicao.idUsuario = usuario.idUsuario
                loadingUI = LoadingUI(binding.btnRegister, binding.progressCircular, null)
                loadingUI.btnToLoading()
                binding.btnExcluirPerfil.visibility = View.GONE
                viewModel.updateUser(usuario.idInfoConta, newInstituicao) {
                    if (it) {
                        sharedViewModel.setUser(newInstituicao)
                        customToast.showCustomToast(
                            "Perfil atualizado com sucesso",
                            Types.SUCESS
                        )
                        val delayMillis = 2000L // 2 segundos

                        // Use um Handler para ocultar a Toast após o atraso
                        Handler(Looper.getMainLooper()).postDelayed({
                            Navigation.findNavController(binding.root)
                                .navigate(R.id.action_registerInstituicaoFragment_to_homeFragment)
                        }, delayMillis)
                    } else {
                        // O registro não foi bem-sucedido
                        loadingUI.loadingToBtn()
                        binding.btnExcluirPerfil.visibility = View.VISIBLE
                        customToast.showCustomToast("Erro ao atualizar perfil", Types.ERROR)
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
