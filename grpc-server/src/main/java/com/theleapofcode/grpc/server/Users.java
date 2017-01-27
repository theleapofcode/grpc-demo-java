package com.theleapofcode.grpc.server;

import java.util.ArrayList;

import com.theleapofcode.grpc.Messages;

public class Users extends ArrayList<Messages.User> {

	private static final long serialVersionUID = 1L;

	private static Users users;

	public static Users getInstance() {
		if (users == null) {
			users = new Users();
		}
		return users;
	}

	private Users() {
		this.add(Messages.User.newBuilder().setId(1).setFirstName("Tony").setLastName("Stark")
				.setEmail("ironman@avengers.com").build());

		this.add(Messages.User.newBuilder().setId(2).setFirstName("Steve").setLastName("Rogers")
				.setEmail("captainamerica@avengers.com").build());

		this.add(Messages.User.newBuilder().setId(3).setFirstName("Bruce").setLastName("Banner")
				.setEmail("hulk@avengers.com").build());
	}

}
