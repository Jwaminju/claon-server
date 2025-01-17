package coLaon.ClaonBack.post.service;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.post.domain.ClimbingHistory;
import coLaon.ClaonBack.post.domain.PostContents;
import coLaon.ClaonBack.post.repository.ClimbingHistoryRepository;
import coLaon.ClaonBack.post.repository.PostLikeRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.post.repository.PostRepositorySupport;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.dto.CenterClimbingHistoryResponseDto;
import coLaon.ClaonBack.user.dto.UserCenterPreviewResponseDto;
import coLaon.ClaonBack.user.dto.ClimbingHistoryResponseDto;
import coLaon.ClaonBack.user.dto.HoldInfoResponseDto;
import coLaon.ClaonBack.user.dto.UserPostDetailResponseDto;
import coLaon.ClaonBack.user.dto.UserPostThumbnailResponseDto;
import coLaon.ClaonBack.user.service.PostPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostToUserAdapter implements PostPort {
    private final PostRepository postRepository;
    private final ClimbingHistoryRepository climbingHistoryRepository;
    private final PaginationFactory paginationFactory;
    private final PostRepositorySupport postRepositorySupport;
    private final PostLikeRepository postLikeRepository;

    @Override
    public Pagination<UserPostThumbnailResponseDto> findPostsByUser(User user, Pageable pageable) {
        return this.paginationFactory.create(
                postRepository.findByWriterAndIsDeletedFalse(user, pageable)
                        .map(post -> UserPostThumbnailResponseDto.from(
                                post.getId(),
                                post.getThumbnailUrl(),
                                post.getCenter().getName(),
                                post.getClimbingHistoryList().stream()
                                        .map(history -> ClimbingHistoryResponseDto.from(
                                                HoldInfoResponseDto.of(
                                                        history.getHoldInfo().getId(),
                                                        history.getHoldInfo().getName(),
                                                        history.getHoldInfo().getImg(),
                                                        history.getHoldInfo().getCrayonImageUrl()
                                                ),
                                                history.getClimbingCount()
                                        ))
                                        .collect(Collectors.toList())
                        ))
        );
    }

    @Override
    public List<String> selectPostIdsByUserId(String userId) {
        return this.postRepository.selectPostIdsByUserId(userId);
    }

    @Override
    public List<CenterClimbingHistoryResponseDto> findClimbingHistoryByPostIds(List<String> postIds) {
        List<ClimbingHistory> climbingHistories = climbingHistoryRepository.findByPostIds(postIds);

        Map<UserCenterPreviewResponseDto, Map<HoldInfoResponseDto, Integer>> historyMap = climbingHistories.stream().collect(
                Collectors.groupingBy(history -> UserCenterPreviewResponseDto.of(
                                history.getPost().getCenter().getThumbnailUrl(),
                                history.getPost().getCenter().getName()
                        ),
                        Collectors.toMap(
                                history -> HoldInfoResponseDto.of(
                                        history.getHoldInfo().getId(),
                                        history.getHoldInfo().getName(),
                                        history.getHoldInfo().getImg(),
                                        history.getHoldInfo().getCrayonImageUrl()
                                ),
                                ClimbingHistory::getClimbingCount,
                                Integer::sum
                        )
                ));

        return historyMap.entrySet()
                .stream()
                .map(entry -> CenterClimbingHistoryResponseDto.from(
                        entry.getKey(),
                        entry.getValue().entrySet()
                                .stream()
                                .map(en -> ClimbingHistoryResponseDto.from(en.getKey(), en.getValue()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public Pagination<UserPostDetailResponseDto> findLaonPost(User user, Pageable pageable) {
        return this.paginationFactory.create(
                postRepositorySupport.findLaonUserPostsExceptBlockUser(user.getId(), pageable).map(
                        post -> UserPostDetailResponseDto.from(
                                post.getId(),
                                post.getCenter().getId(),
                                post.getCenter().getName(),
                                post.getWriter().getImagePath(),
                                post.getWriter().getNickname(),
                                postLikeRepository.findByLikerAndPost(user, post).isPresent(),
                                postLikeRepository.countByPost(post),
                                post.getContent(),
                                post.getCreatedAt(),
                                post.getContentList().stream().map(PostContents::getUrl).collect(Collectors.toList()),
                                post.getClimbingHistoryList().stream()
                                .map(history -> ClimbingHistoryResponseDto.from(
                                        HoldInfoResponseDto.of(
                                                history.getHoldInfo().getId(),
                                                history.getHoldInfo().getName(),
                                                history.getHoldInfo().getImg(),
                                                history.getHoldInfo().getCrayonImageUrl()
                                        ),
                                        history.getClimbingCount()
                                ))
                                .collect(Collectors.toList()))
                )
        );
    }
}
