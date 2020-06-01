package cn.edu.scujcc.service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.edu.scujcc.dao.ChannelRepository;
import cn.edu.scujcc.model.Channel;
import cn.edu.scujcc.model.Comment;
/**
 * 提供频道相关的业务逻辑
 * @author DELL
 *
 */
@Service
public class ChannelService {
	@Autowired
	private ChannelRepository repo;
	
	/**
	 * 获取所有频道的数据
	 * @return	频道List
	 */
	public List<Channel> getAllChannels(){
		repo.findByTitleContaining("中央"); 			//返回值为包含中央两个字的值
		return repo.findAll();
	}
	/**
	 * 获取一个频道
	 * @param id
	 * @return
	 */
	public Channel getChannel(String channelId) {
		
		 Optional<Channel> result=repo.findById(channelId);
		 if(result.isPresent()) {
			 return result.get();
		 }else {
			 return null;
		 }
		 
	}
	
	/**
	 * 删除指定频道
	 * @param id
	 * @return
	 */
	public boolean deleteChannel(String channelId) { 
		boolean result = true ;
		repo.deleteById(channelId);
		
		return result;
	}
	/**
	 * 更新一个频道
	 * @param c待更新的频道
	 * @return更新后的频道
	 */
	public Channel updateChannel(Channel c) {
		 Channel saved =getChannel(c.getId());
		 if(saved !=null) {
			 if(c.getTitle()!=null) {
				 saved.setTitle(c.getTitle());
			 }
			 if(c.getQuality()!=null) {
				 saved.setQuality(c.getQuality());
			 }
			 if(c.getUrl()!=null) {
				 saved.setUrl(c.getUrl());
			 }
			 if(c.getComments()!=null) {
				 if(saved.getComments() != null) {//把新评论追加到老评论后面
					 saved.getComments().addAll(c.getComments());
				 }else {//用新评论代替老评论
					 saved.setComments(c.getComments());
				 }
				 
			 }
		 }
		 if(c.getCover() != null) {
			 saved.setCover(c.getCover());
		 }
		return repo.save(saved);//保存更新后的实体对象
	}
	/**
	 * 新建频道
	 * @param c
	 * @return
	 */
	public Channel createChannel(Channel c) {

		return repo.save(c);
	}
	/**
	 * 搜索方法
	 * @param title
	 * @param quality
	 * @return
	 */
	public List<Channel> searchByTitle(String title){
		return repo.findByTitle(title);
	}
	public List<Channel> searchByQuality(String quality){
		return repo.findByQuality(quality);
}
	public List<Channel> getLatestCommentsChannel(){
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime today = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0);
		return repo.findByCommentsDtAfter(today);
	}
	/**
	 * 向指定的频道增加一条评论
	 * @param channelId	指定评论的编号
	 * @param comment	将要增加的评论对象
	 */
	public Channel addComment(String channelId, Comment comment) {
		Channel saved = getChannel(channelId);
		if(saved !=null) {
			//防止空指针
			if(saved.getComments() == null) {
				saved.setComments(new ArrayList<>());
			}
			saved.getComments().add(comment);
			return repo.save(saved);
		}
		return null;
	}
	/**
	 * 返回指定频道的热门评论
	 * @param channelId	指定频道的编号
	 * @return	热门评论的列表
	 */
	public List<Comment> hotcomments(String channelId){
		List<Comment> result = new ArrayList<>();
		Channel saved =getChannel(channelId);
		
		if(saved != null && saved.getComments()!=null) {
			//根据评论的star进行排序
			saved.getComments().sort(new Comparator<Comment>() {

				@Override
				public int compare(Comment o1, Comment o2) {
					if(o1.getStar() == o2.getStar()) {
						return 0;
					}else if(o1.getStar() < o2.getStar()) {
						return 1;
					}else {
						return -1;
					}
				}
			});
			
			if(saved.getComments().size()>3) {
				result = saved.getComments().subList(0,3);
			}else {
				result = saved.getComments();
			}
		}
		return result;
	}
}
