package com.example.study.account;

import com.example.study.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import javax.validation.constraints.AssertTrue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("회원 가입 보이는지")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("account/sign-up"))
                    .andExpect(model().attributeExists("signUpForm"));
    }

    @DisplayName("회원 가입 처리")
    @Test
    void  signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "Dongyeon")
                        .param("email", "dongyeon94@naver.com")
                        .param("password", "1234aAbBcC12!@")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));
    }

    @DisplayName("회원 가입 처리 - 입력값 정상")
    @Test
    void  signUpSubmit_with_correct_input() throws Exception {
        mockMvc.perform(post("/sign-up")
            .param("nickname", "dongyeon")
            .param("email", "dongyeon94@naver.com")
            .param("password", "1234aAbBcC12!@")
            .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/"));

        Account account = accountRepository.findByEmail("dongyeon94@naver.com");
        assertNotNull(account);
        assertNotEquals(account.getPassword(), "1234aAbBcC12!@");
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }
}
