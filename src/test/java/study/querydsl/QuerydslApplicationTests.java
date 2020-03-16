package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Hello;
import study.querydsl.entity.QHello;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
@Commit
class QuerydslApplicationTests {

	@Autowired
	//@PersistenceContext
	EntityManager entityManager;

	@Test
	void contextLoads() {
		Hello hello = new Hello();
		entityManager.persist(hello);

		JPAQueryFactory query =  new JPAQueryFactory(entityManager);
		QHello qHello = new QHello("h");

		Hello result = query.selectFrom(qHello)
				.fetchOne();
		assertThat(result).isEqualTo(hello);

	}

}
