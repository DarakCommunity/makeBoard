package darak.study.spring_study.service;

import darak.study.spring_study.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    private final MemberRepository memberRepository;

    public SpringConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Bean public MemberService memberService() {
        return new MemberService(memberRepository);
    }
}
