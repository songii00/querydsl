package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.swing.*;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.member;

import java.util.List;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager entityManager;
    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before(){
        // 동시성 문제 없음
        // 멀티 스레드 에 문제 없이 설계
        queryFactory = new JPAQueryFactory(entityManager);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        entityManager.persist(teamA);
        entityManager.persist(teamB);


        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        entityManager.persist(member1);
        entityManager.persist(member2);
        entityManager.persist(member3);
        entityManager.persist(member4);
    }

    @Test
    public void startJPQL(){
        // member1 을 찾아라
        String qlString = "select m " +
                "from Member m " +
                "where m.username = :username";
        Member findMember = entityManager.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl(){

        QMember m = new QMember("m");
        Member findMember = queryFactory.select(m)
                .from(m)
                // 파라미터 바인딩 자동으로 해줌
                // 오타 발생 / 문법 오류 시 컴파일타임 에러 발생 (QType)
                .where(m.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl2(){

        Member findMember = queryFactory.select(member) // 큐타입 스태틱 임포트로 활용
                                        .from(member)
                                        .where(member.username.eq("member1"))
                                        .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search(){
        Member findMember
                = queryFactory.selectFrom(member)
                                     .where(member.username.eq("member1")
                                                           .and(member.age.eq(10)))
                                     .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");

    }

    @Test
    public void searchAndParam(){
        Member findMember
                = queryFactory.selectFrom(member)
                              .where(
                                      member.username.eq("member1"),
                                      member.age.eq(10)
                              )
                              .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");

    }


    @Test
    public void resultFetch(){

        /**
         * 주의 > 컨텐츠 가져오는 쿼리가 복잡하면 성능을 위해서 토탈카운트 쿼리와 컨텐츠 가져오는 쿼리가 다를수 있음
         * 복잡하고 성능 중요한 쿼리에선 사용하면 안됨
         */
        QueryResults<Member> results = queryFactory.selectFrom(member)
                                                              .fetchResults();

        // 카운트 쿼리
        // select
        //        count(member0_.member_id) as col_0_0_
        //    from
        //        member member0_
        results.getTotal();
        // 컨텐츠 쿼리
        //  select
        //        member0_.member_id as member_i1_1_,
        //        member0_.age as age2_1_,
        //        member0_.team_id as team_id4_1_,
        //        member0_.username as username3_1_
        //    from
        //        member member0_
        List<Member> contents = results.getResults();

        // 카운트 절로 바꿈
        // select
        //        count(member0_.member_id) as col_0_0_
        //    from
        //        member member0_
        long count = queryFactory.selectFrom(member)
                             .fetchCount();
    }


}
