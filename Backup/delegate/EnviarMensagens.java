package com.mycompany.myapp.delegate;

import java.util.Locale;

import com.mycompany.myapp.service.MailService;
import com.mycompany.myapp.service.dto.ControlePontosDTO;
import com.mycompany.myapp.service.dto.ControlePontosProcessDTO;
import com.mycompany.myapp.service.dto.FuncionarioDTO;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Component
public class EnviarMensagens implements JavaDelegate {

    @Autowired
    MailService mailService;

    @Autowired
    SpringTemplateEngine templateEngine;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("+++++++++++++++++++++++");
        System.out.println(delegateExecution.getVariable("processInstance").getClass().getName());
        System.out.println(delegateExecution.getVariable("processInstance"));
        System.out.println("+++++++++++++++++++++++");

        ControlePontosProcessDTO pi = (ControlePontosProcessDTO) delegateExecution.getVariable("processInstance");
        ControlePontosDTO controlePontos = pi.getControlePontos();
        FuncionarioDTO funcionario = controlePontos.getCpfDoFuncionario();

        String to = funcionario.getEmail();
        String subject = "[AgileKip] Email padrao " + funcionario.getNome();
        Context context = new Context(Locale.getDefault());
        context.setVariable("controlePontos", controlePontos);
        context.setVariable("funcionario", funcionario);

        Integer tipoAlt = controlePontos.getTipoAlteracao();
        String content = "";

        if (tipoAlt == 0) {
            content = templateEngine.process("controlePontosProcess/inserirHorasEmail", context);
        }
        else if (tipoAlt == 1) {
            content = templateEngine.process("controlePontosProcess/alterarHorasEmail", context);
        }
        else if (tipoAlt == 2) {
            content = templateEngine.process("controlePontosProcess/deletarHorasEmail", context);
        }

        System.out.println("+++++++++++++++++++++++");
        System.out.println("Enviando email para: " + to);            
        System.out.println("Assunto: " + subject);            
        System.out.println("+++++++++++++++++++++++");
        
        mailService.sendEmail(to, subject, content, false, true);
    }
}
