package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallTopicMapper;
import com.attitude.tinymall.domain.LitemallTopic;
import com.attitude.tinymall.domain.LitemallTopicExample;
import com.attitude.tinymall.service.LitemallTopicService;
import com.github.pagehelper.PageHelper;
import java.util.List;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LitemallTopicServiceImpl implements LitemallTopicService {
    @Autowired
    private LitemallTopicMapper topicMapper;
    @Override
    public List<LitemallTopic> queryList(int offset, int limit) {
        LitemallTopicExample example = new LitemallTopicExample();
        example.or().andDeletedEqualTo(false);
        PageHelper.startPage(offset, limit);
        return topicMapper.selectByExampleWithBLOBs(example);
    }
    @Override
    public int queryTotal() {
        LitemallTopicExample example = new LitemallTopicExample();
        example.or().andDeletedEqualTo(false);
        return (int)topicMapper.countByExample(example);
    }
    @Override
    public LitemallTopic findById(Integer id) {
        LitemallTopicExample example = new LitemallTopicExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return topicMapper.selectOneByExampleWithBLOBs(example);
    }
    @Override
    public List<LitemallTopic> queryRelatedList(Integer id, int offset, int limit) {
        LitemallTopicExample example = new LitemallTopicExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        List<LitemallTopic> topics = topicMapper.selectByExample(example);
        if(topics.size() == 0){
            return queryList(offset, limit);
        }
        LitemallTopic topic = topics.get(0);

        example = new LitemallTopicExample();
        example.or().andIdNotEqualTo(topic.getId()).andDeletedEqualTo(false);
        PageHelper.startPage(offset, limit);
        List<LitemallTopic> relateds = topicMapper.selectByExampleWithBLOBs(example);
        if(relateds.size() != 0){
            return relateds;
        }

        return queryList(offset, limit);
    }
    @Override
    public List<LitemallTopic> querySelective(String title, String subtitle, Integer page, Integer limit, String sort, String order) {
        LitemallTopicExample example = new LitemallTopicExample();
        LitemallTopicExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(title)){
            criteria.andTitleLike("%" + title + "%");
        }
        if(!StringUtils.isEmpty(subtitle)){
            criteria.andSubtitleLike("%" + subtitle + "%");
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, limit);
        return topicMapper.selectByExampleWithBLOBs(example);
    }
    @Override
    public int countSelective(String title, String subtitle, Integer page, Integer size, String sort, String order) {
        LitemallTopicExample example = new LitemallTopicExample();
        LitemallTopicExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(title)){
            criteria.andTitleLike("%" + title + "%");
        }
        if(!StringUtils.isEmpty(subtitle)){
            criteria.andSubtitleLike("%" + subtitle + "%");
        }
        criteria.andDeletedEqualTo(false);

        return (int)topicMapper.countByExample(example);
    }
    @Override
    public void updateById(LitemallTopic topic) {
        LitemallTopicExample example = new LitemallTopicExample();
        example.or().andIdEqualTo(topic.getId());
        topicMapper.updateByExampleWithBLOBs(topic, example);
    }
    @Override
    public void deleteById(Integer id) {
        topicMapper.logicalDeleteByPrimaryKey(id);
    }
    @Override
    public void add(LitemallTopic topic) {
        topicMapper.insertSelective(topic);
    }


}
