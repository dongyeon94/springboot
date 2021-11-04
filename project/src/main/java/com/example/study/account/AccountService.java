package com.example.study.account;

import com.example.study.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    private void signUpConfirmEmail(Account account) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(account.getEmail());
        simpleMailMessage.setSubject("스터디 올래 회원 가입인증");
        simpleMailMessage.setText("/check-email-token?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        javaMailSender.send(simpleMailMessage);
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm){
        Account account = Account.builder()
            .email(signUpForm.getEmail())
            .nickname(signUpForm.getNickname())
            .password(passwordEncoder.encode(signUpForm.getPassword()))
            .studyCreatedByWeb(true)
            .studyEnrollmentResultByWeb(true)
            .studyUpdateByWeb(true)
            .build();
        return accountRepository.save(account);
    }

    @Transactional
    public void processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        signUpConfirmEmail(newAccount);
    }
}
