package com.example.voluntask.ui

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.voluntask.R
import com.example.voluntask.databinding.FragmentInscricaoBinding
import com.example.voluntask.models.Evento
import com.example.voluntask.models.Inscricao
import com.example.voluntask.models.Usuario
import com.example.voluntask.models.enums.Categorias
import com.example.voluntask.models.enums.Status
import com.example.voluntask.util.CustomToast
import com.example.voluntask.util.LoadingUI
import com.example.voluntask.util.Types
import com.example.voluntask.viewmodels.InscricaoViewModel
import com.example.voluntask.viewmodels.SharedViewModel
import java.time.LocalDate


class InscricaoFragment : Fragment() {

    private lateinit var binding: FragmentInscricaoBinding
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val evento = arguments?.getParcelable<Evento>("evento")
        binding = FragmentInscricaoBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(requireActivity())[InscricaoViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        val customToast = CustomToast(binding.customToast, requireContext())
        var loadingUI: LoadingUI
        var usuarioInscrito: Boolean = false
        var idInscricao: String = ""

        sharedViewModel.usuario.observe(viewLifecycleOwner) { usuario ->
            binding.nomeInst.setText(usuario!!.nome)
            viewModel.inscrito(evento!!.idEvento, usuario.idUsuario) {
                if (it != null) {
                    binding.btnRegister.setBackgroundColor(Color.parseColor("#FF0000"))
                    binding.btnRegister.setText("Cancelar inscrição")
                    usuarioInscrito = true
                    idInscricao = it.idInscricao
                }
                binding.btnRegister.visibility = View.VISIBLE
            }
        }



        if (evento!!.status == Status.ENCERRADO) {
            binding.btnRegister.isEnabled = false
            binding.btnRegister.setBackgroundColor(Color.parseColor("#737373"))
        }


        val dataInicio = LocalDate.parse(evento.dataInicio!!.toString())
        val dataFim = LocalDate.parse(evento.dataFim!!.toString())

        binding.dataS.setText(
            "${"%02d".format(dataInicio.dayOfMonth)}/${
                "%02d".format(dataInicio.month.value + 1)
            }/${dataInicio.year}"
        )


        binding.dataE.setText(
            "${"%02d".format(dataFim.dayOfMonth)}/${
                "%02d".format(dataFim.month.value + 1)
            }/${dataFim.year}"
        )


        binding.categoria.setText(
            when (evento.categoria) {
                Categorias.DOACAO -> "Doação"
                Categorias.LIMPEZA -> "Limpeza"
                Categorias.CARIDADE -> "Caridade"
            }
        )

        binding.status.setText(
            when (evento.status) {
                Status.ATIVO -> "Ativo"
                Status.ENCERRADO -> "Encerrado"
            }
        )



        binding.name.setText(evento.nome)
        binding.local.setText(evento.localizacao)
        binding.desc.setText(evento.descricao)

        binding.btnRegister.setOnClickListener {
            if (usuarioInscrito) {
                viewModel.deletarInscricao(idInscricao) {
                    loadingUI = LoadingUI(binding.btnRegister, binding.progressCircular, null)
                    loadingUI.btnToLoading()
                    if (it) {
                        customToast.showCustomToast("Inscrição deletada com sucesso", Types.SUCESS)
                        val delayMillis = 2000L // 2 segundos

                        // Use um Handler para ocultar a Toast após o atraso
                        Handler(Looper.getMainLooper()).postDelayed({
                            Navigation.findNavController(binding.root)
                                .navigate(R.id.action_inscricaoFragment_to_homeFragment)
                        }, delayMillis)
                    } else {
                        // O registro não foi bem-sucedido
                        loadingUI.loadingToBtn()
                        customToast.showCustomToast("Erro ao deletar inscrição", Types.ERROR)
                    }
                }
            } else {
                sharedViewModel.usuario.observe(viewLifecycleOwner) { usuario ->
                    val inscricao =
                        Inscricao(evento.idEvento, usuario!!.idUsuario, LocalDate.now().toString())

                    loadingUI = LoadingUI(binding.btnRegister, binding.progressCircular, null)
                    loadingUI.btnToLoading()
                    viewModel.createInscricao(inscricao) {
                        if (it) {
                            customToast.showCustomToast(
                                "Inscrição registrada com sucesso",
                                Types.SUCESS
                            )
                            val delayMillis = 2000L // 2 segundos

                            // Use um Handler para ocultar a Toast após o atraso
                            Handler(Looper.getMainLooper()).postDelayed({
                                Navigation.findNavController(binding.root)
                                    .navigate(R.id.action_inscricaoFragment_to_homeFragment)
                            }, delayMillis)
                        } else {
                            // O registro não foi bem-sucedido
                            loadingUI.loadingToBtn()
                            customToast.showCustomToast("Erro ao registrar inscrição", Types.ERROR)
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
            InscricaoFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}