package com.mycompany.myapp.delegate;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.mycompany.myapp.domain.ControlePontos;
import com.mycompany.myapp.domain.ControlePontosProcess;
import com.mycompany.myapp.domain.Funcionario;
import com.mycompany.myapp.domain.Ponto;
import com.mycompany.myapp.repository.ControlePontosRepository;
import com.mycompany.myapp.repository.ControlePontosProcessRepository;
import com.mycompany.myapp.repository.FuncionarioRepository;
import com.mycompany.myapp.repository.PontoRepository;
import com.mycompany.myapp.service.dto.ControlePontosDTO;
import com.mycompany.myapp.service.dto.ControlePontosProcessDTO;
import com.mycompany.myapp.service.dto.FuncionarioDTO;
import com.mycompany.myapp.service.dto.PontoDTO;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecalcularHoras implements JavaDelegate {

    private final ControlePontosRepository controlePontosRepo;
    private final FuncionarioRepository funcionarioRepo;
    private final PontoRepository pontoRepo;

    public RecalcularHoras(
        ControlePontosRepository controlePontosRepository,
        FuncionarioRepository funcionarioRepo,
        PontoRepository pontoRepo
    ) {
        this.controlePontosRepo = controlePontosRepository;
        this.funcionarioRepo = funcionarioRepo;
        this.pontoRepo = pontoRepo;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        System.out.println("+++++++++++++++++++++++");
        System.out.println(delegateExecution.getVariable("processInstance").getClass().getName());
        System.out.println(delegateExecution.getVariable("processInstance"));
        System.out.println("+++++++++++++++++++++++");

        ControlePontosProcessDTO pi = (ControlePontosProcessDTO) delegateExecution.getVariable("processInstance");
        ControlePontosDTO controlePontosDTO = pi.getControlePontos();
        ControlePontos controlePontosObj = controlePontosRepo.findById(controlePontosDTO.getId()).get();
	
        Funcionario cpfDoFuncionario = controlePontosObj.getCpfDoFuncionario();
        Integer tipoAlteracao = controlePontosObj.getTipoAlteracao();
        
        System.out.println("+++++++++++++++++++++++");
        System.out.println("CPF:" + cpfDoFuncionario.getCpf());
        System.out.println("TipoAlt:" + tipoAlteracao);
        System.out.println("+++++++++++++++++++++++");

        if (tipoAlteracao == 0) { // Inserir Ponto

            LocalDate dataParaInserir = controlePontosDTO.getInserirData();
            Ponto novoPonto = new Ponto();

            // Aqui tem que decidir como vai ser o id
            // sugiro cpf do funcionario mais a data do ponto convertido em Long.
            // novoPonto.setId(CPF + DATA);

            novoPonto.setDataHoraMarcacao(dataParaInserir);
            novoPonto.setCpfDoFuncionario(cpfDoFuncionario);
            pontoRepo.save(novoPonto);
        }




	/*
        // Set the new values to the DTO and the Object so they are in sync
        controlePontosObj.setStatus(PlanStatus.PAID);
        controlePontosDTO.setStatus(PlanStatus.PAID);

        // save the modified obj to the repo
        controlePontosRepo.save(controlePontosObj);

        if (controlePontosDTO.getPonto() != null) {
            Ponto ponto = this.pontoRepo.findById(controlePontosDTO.getPonto().getId()).get();
            ponto.setEmptySeats(ponto.getEmptySeats() - controlePontosDTO.getNumPax());
            pontoRepo.save(ponto);
        }
        if (controlePontosDTO.getFuncionario() != null) {
            Funcionario funcionario = this.funcionarioRepo.findById(controlePontosDTO.getFuncionario().getId()).get();
            funcionario.setBoodked(controlePontosDTO.getHotelStartDate());
            funcionario.setDuration(controlePontosDTO.getHotelDuration());
            funcionarioRepo.save(funcionario);
        }
	*/
    }
}
