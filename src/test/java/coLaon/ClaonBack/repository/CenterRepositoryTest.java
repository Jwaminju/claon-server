package coLaon.ClaonBack.repository;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterBookmark;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.ChargeElement;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.domain.SectorInfo;
import coLaon.ClaonBack.center.repository.CenterBookmarkRepository;
import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class CenterRepositoryTest {
    @Autowired
    private CenterRepository centerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CenterBookmarkRepository centerBookmarkRepository;

    private User user;

    @BeforeEach
    void setUp() {
        this.user = userRepository.save(User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId"
        ));

        Center center = centerRepository.save(Center.of(
                "test",
                "test",
                "010-1234-1234",
                "https://test.com",
                "https://instagram.com/test",
                "https://youtube.com/channel/test",
                List.of(new CenterImg("img test")),
                List.of(new OperatingTime("매일", "10:00", "23:00")),
                "facilities test",
                List.of(new Charge(List.of(new ChargeElement("자유 패키지", "330,000")), "charge image")),
                "hold info img test",
                List.of(new SectorInfo("test sector", "1/1", "1/2"))
        ));

        this.centerBookmarkRepository.save(CenterBookmark.of(center, this.user));
    }

    @Test
    public void successSearchCenter() {
        // given
        String keyword = "tes";

        // when
        List<Center> centerList = centerRepository.searchCenter(keyword);

        // then
        assertThat(centerList.size()).isEqualTo(1);
    }

    @Test
    public void successFindNewlyCreatedCenter() {
        // given
        LocalDateTime before7days = LocalDate.now().atStartOfDay().minusDays(7);

        // when
        Page<Center> page = centerRepository.findNewlyCreatedCenter(before7days, PageRequest.of(0, 2));

        // then
        assertThat(page.getTotalElements()).isEqualTo(1);

        // given
        LocalDateTime standardDate = LocalDate.now().atStartOfDay().plusDays(7);

        // when
        Page<Center> newPage = centerRepository.findNewlyCreatedCenter(standardDate, PageRequest.of(0, 2));

        // then
        assertThat(newPage.getTotalElements()).isEqualTo(0);
    }

    @Test
    public void successFindBookmarkedCenter() {
        // given
        String userId = user.getId();

        // when
        Page<Center> page = centerRepository.findBookmarkedCenter(userId, PageRequest.of(0, 2));

        // then
        assertThat(page.getTotalElements()).isEqualTo(1);
    }
}
