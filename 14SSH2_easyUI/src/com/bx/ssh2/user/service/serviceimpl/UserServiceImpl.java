package com.bx.ssh2.user.service.serviceimpl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bx.ssh2.base.util.Encrypt;
import com.bx.ssh2.user.dao.UserDao;
import com.bx.ssh2.user.pageModal.UserModal;
import com.bx.ssh2.user.pojo.User;
import com.bx.ssh2.user.service.UserService;

@Service(value="userService")
@Transactional
public class UserServiceImpl implements UserService{
	@Autowired
	private UserDao userDao;
	@Override
	public void regUser(User user) {
		user.setId(UUID.randomUUID().toString());
		user.setPassword(Encrypt.e(user.getPassword()));
		user.setCreateTime(new Date());
		userDao.save(user);
	}
	@Override
	public boolean loginUser(User user) {
		user.setPassword(Encrypt.e(user.getPassword()));
		String hql = "from User where name = ? and password = ?";
		return userDao.login(hql , user.getName(),user.getPassword());
	}
	@Override
	public UserModal paging(int rows,int page,String searchId) {
		String hql = "from User";
		if(searchId != null && !("").equals(searchId)){
			hql = hql + " where name like '%" + searchId + "%'";
		}
		List<User> list = userDao.paging(hql , Integer.valueOf(rows), Integer.valueOf(page));
		UserModal um = new UserModal();
		um.setRows(list);
		hql = "select count(id) from User";
		um.setTotal(userDao.count(hql));
		return um;
	}
	@Override
	public boolean delete(String ids) {
		String[] idStr = ids.split(",");
		return userDao.delete(idStr);
	}
	@Override
	public void change(User user) {
		User userById = userDao.findById(user, user.getId());
		userById.setPassword(Encrypt.e(user.getPassword()));
		userById.setModifyTime(new Date());
		userDao.update(userById);
	}
}
