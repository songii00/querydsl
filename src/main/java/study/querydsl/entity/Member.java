package study.querydsl.entity;

import lombok.*;

import javax.jdo.annotations.Join;
import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 연관관계가 걸려있는 toString은 무한루프 가능성 있음
// toString 사용하지 않기
@ToString(of = {"id, username", "age"})
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team !=null){
            changeTeam(team);
        }
    }

    public Member(String username, int age) {
       this(username, age, null);
    }

    public Member(String username){
        this(username, 0);
    }

    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }
}
