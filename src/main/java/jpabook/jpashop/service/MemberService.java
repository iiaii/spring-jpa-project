package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // 컴포넌트 스캔 대상
@Transactional(readOnly = true) // JPA의 데이터 변경은 트랜잭션이 있어야 함 (그래야 Lazy 로딩도 다 됨)
@RequiredArgsConstructor // final 필드만 생성자 만들어줌 -> 스프링이 자동으로 autowired 함
public class MemberService {

    // @Autowired  // 필드 인젝션 (더 좋은 방법도 있음 -> 생성자 인젝션)
    private final MemberRepository memberRepository; // 컴파일 시점에 값 세팅 안하면 알수도 있고 변경도 안됨

    // @Autowired // 생상자 인젝션 (테스트 케이스 작성시 목 객체를 넣어주는 걸 명확하게 알 수 있음)
// -> 최근에는 @Autowired 안해도 주입됨 -> 롬복 어노테이션으로 @AllArgsConstructor (생성자 생성) -> 롬복 @RequiredArgsConstroctor
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    // 세터 인젝션 (중간에 테스트 목객체를 주입해 줄 수 있음) - 안 좋음 (변경될 여지)
//    @Autowired
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /**
     * 회원 가입
     */
    @Transactional // 기본적으로 readOnly false - 쓰기에 적합
    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        // 실무에서는 멀티 스레드 환경을 고려해서 (동시에 같은 이름으로 가입하는 경우) DB의 멤버의 name을 유니크 제약 조건으로 잡아주어야 함
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    // @Transactional(readOnly = true) - 조회하는 곳에서 좀더 최적화함 (읽기에는 readOnly = true, 변경에는 넣으면 안됨)
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
