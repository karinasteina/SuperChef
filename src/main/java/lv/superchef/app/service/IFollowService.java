package lv.superchef.app.service;

public interface IFollowService {
    public void follow(Long followerId, Long followingId);
    public void unfollow(Long followerId, Long followingId);
}
