package com.example.voluntask.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.voluntask.R
import com.example.voluntask.databinding.FragmentRegisterVoluntarioBinding
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
import java.time.LocalDate
import java.util.Calendar


class RegisterVoluntarioFragment : Fragment() {

    private lateinit var binding: FragmentRegisterVoluntarioBinding
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
        binding = FragmentRegisterVoluntarioBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        val customToast = CustomToast(binding.customToast, requireContext())
        val items = listOf("Masculino", "Feminino", "Prefiro não dizer")
        val usuario = arguments?.getParcelable<Usuario>("usuario") as Voluntario?
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        var ano = "";
        var mes = "";
        var dia = "";
        var idade = 0
        var genero: Generos = Generos.PREFIRO_NAO_DIZER
        val mYear = Calendar.getInstance().get(Calendar.YEAR)
        var loadingUI: LoadingUI
        (binding.inputGeneroLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)


        if (usuario != null) {
            binding.h1.text = "Editar Perfil"
            binding.btnCadastrar.text = "Salvar"
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
            binding.btnExcluirPerfil.visibility = View.VISIBLE
            binding.inputTelefone.addTextChangedListener(MaskChangedListener(telefoneMask))
            binding.inputCpf.addTextChangedListener(MaskChangedListener(cpfMask))
            binding.inputTelefone.setText(usuario.telefone)
            binding.inputCpf.setText(usuario.cpf)
            binding.inputEmail.setText(usuario.email)
            binding.topSpace.layoutParams.height = 250
            binding.inputSenhaLayout.visibility = View.GONE
            binding.inputConfirmarSenhaLayout.visibility = View.GONE
            binding.inputEmailLayout.isEnabled = false
            binding.inputEmail.isEnabled = false
            binding.inputNome.setText(usuario.nome)
            val dataNascimento = LocalDate.parse(usuario.dataNascimento.toString())

            val selectedGeneroPosition = when (usuario.genero) {
                Generos.MASCULINO -> 0
                Generos.FEMININO -> 1
                Generos.PREFIRO_NAO_DIZER -> 2
            }

            val selectedItem = when (selectedGeneroPosition) {
                0 -> "MASCULINO"
                1 -> "FEMININO"
                2 -> "PREFIRO_NAO_DIZER"
                else -> ""
            }
            genero = Generos.fromValue(selectedItem)

            binding.inputGenero.setText(
                binding.inputGenero.adapter.getItem(selectedGeneroPosition).toString(), false
            );


            dia = "%02d".format(dataNascimento.dayOfMonth)
            mes = dataNascimento.month.value.toString()
            ano = dataNascimento.year.toString()


            idade = mYear - dataNascimento.year

            binding.inputData.setText(
                "${"%02d".format(dataNascimento.dayOfMonth)}/${
                    "%02d".format(dataNascimento.month.value)
                }/${dataNascimento.year}"
            )
        } else {
            binding.inputEmail.setCompoundDrawables(null, null, null, null)
        }

        binding.h1.visibility = View.VISIBLE
        binding.btnCadastrar.visibility = View.VISIBLE


        binding.btnExcluirPerfil.setOnClickListener {
            val bundle = bundleOf("usuario" to usuario)
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_registerVoluntarioFragment_to_excluirPerfilFragment, bundle)
        }


        binding.inputData.setOnClickListener(View.OnClickListener { // calender class's instance and get current date , month and year from calender
            val c: Calendar = Calendar.getInstance()
            val mMonth = Calendar.getInstance().get(Calendar.MONTH)
            val mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            context?.let { it1 ->
                DatePickerDialog(
                    it1,
                    { _, year, monthOfYear, dayOfMonth -> // set day of month , month and year value in the edit text
                        idade = mYear - year
                        ano = year.toString()
                        mes = "%02d".format(monthOfYear + 1)
                        dia = "%02d".format(dayOfMonth)
                        val date = "$dia/$mes/$ano"
                        binding.inputData.setText(date)
                    },
                    mYear,
                    mMonth,
                    mDay
                )
            }?.show()
        })


        binding.inputGenero.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = when (position) {
                0 -> "MASCULINO"
                1 -> "FEMININO"
                2 -> "PREFIRO_NAO_DIZER"
                else -> ""
            }
            genero = Generos.fromValue(selectedItem)
        }

        binding.btnCadastrar.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            val senha = binding.inputSenha.text.toString()
            val nome = binding.inputNome.text.toString()
            val telefone = binding.inputTelefone.unMasked
            val cpf = binding.inputCpf.unMasked
            val dataNascimento = binding.inputData.text.toString()
            val confirmarSenha = binding.inputConfirmarSenha.text.toString()
            val dataCadastro = LocalDate.now()
            val tipoConta = TipoConta.VOLUNTARIO

            if (usuario == null) {
                if (
                    nome.isBlank() ||
                    email.isBlank() ||
                    telefone.isBlank() ||
                    dataNascimento.isBlank() ||
                    binding.inputGenero.text.toString().isBlank() ||
                    cpf.isBlank() ||
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
                } else if (!binding.inputCpf.isDone) {
                    customToast.showCustomToast("Cpf inválido", Types.ERROR)
                } else if (!binding.inputTelefone.isDone) {
                    customToast.showCustomToast("Número de telefone inválido", Types.ERROR)
                } else if (idade < 18) {
                    customToast.showCustomToast("Idade menor que 18 anos", Types.ERROR)
                } else {
                    val newVoluntario = Voluntario(
                        nome,
                        telefone,
                        "${dataCadastro.year}-${dataCadastro.month.value}-${dataCadastro.dayOfMonth}",
                        tipoConta,
                        "",
                        "$ano-$mes-$dia",
                        cpf,
                        genero
                    )
                    loadingUI = LoadingUI(binding.btnCadastrar, binding.progressCircular, null)
                    loadingUI.btnToLoading()
                    if (usuario == null) {
                        viewModel.register(newVoluntario, email, senha) {
                            if (it.result) {
                                customToast.showCustomToast(it.msg, Types.SUCESS)
                                val delayMillis = 2000L // 2 segundos

                                // Use um Handler para ocultar a Toast após o atraso
                                Handler(Looper.getMainLooper()).postDelayed({
                                    Navigation.findNavController(binding.root)
                                        .navigate(R.id.action_registerVoluntarioFragment_to_loginFragment)
                                }, delayMillis)
                            } else {
                                // O registro não foi bem-sucedido
                                loadingUI.loadingToBtn()
                                customToast.showCustomToast(it.msg, Types.ERROR)
                            }
                        }
                    }
                }
            } else {
                if (
                    nome.isBlank() ||
                    email.isBlank() ||
                    telefone.isBlank() ||
                    dataNascimento.isBlank() ||
                    binding.inputGenero.text.toString().isBlank() ||
                    cpf.isBlank()
                ) {
                    customToast.showCustomToast(
                        "Todos os campos precisam ser preenchidos",
                        Types.WARNING
                    )
                } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString())
                        .matches()
                ) {
                    customToast.showCustomToast("E-mail inválido", Types.ERROR)
                } else if (!binding.inputCpf.isDone) {
                    customToast.showCustomToast("Cpf inválido", Types.ERROR)
                } else if (!binding.inputTelefone.isDone) {
                    customToast.showCustomToast("Número de telefone inválido", Types.ERROR)
                } else if (idade < 18) {
                    customToast.showCustomToast("Idade menor que 18 anos", Types.ERROR)
                } else {
                    val newVoluntario = Voluntario(
                        nome,
                        telefone,
                        "${dataCadastro.year}-${dataCadastro.month.value}-${dataCadastro.dayOfMonth}",
                        tipoConta,
                        "",
                        "$ano-$mes-$dia",
                        cpf,
                        genero
                    )
                    newVoluntario.idUsuario = usuario.idUsuario
                    newVoluntario.dataCadastro = usuario.dataCadastro
                    newVoluntario.email = usuario.email
                    newVoluntario.idInfoConta = usuario.idInfoConta
                    newVoluntario.idUsuario = usuario.idUsuario

                    loadingUI = LoadingUI(binding.btnCadastrar, binding.progressCircular, null)
                    loadingUI.btnToLoading()
                    binding.btnExcluirPerfil.visibility = View.GONE
                    viewModel.updateUser(usuario.idInfoConta, newVoluntario) {
                        if (it) {
                            sharedViewModel.setUser(newVoluntario)
                            customToast.showCustomToast(
                                "Perfil atualizado com sucesso",
                                Types.SUCESS
                            )
                            val delayMillis = 2000L // 2 segundos

                            // Use um Handler para ocultar a Toast após o atraso
                            Handler(Looper.getMainLooper()).postDelayed({
                                Navigation.findNavController(binding.root)
                                    .navigate(R.id.action_registerVoluntarioFragment_to_homeFragment)
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
