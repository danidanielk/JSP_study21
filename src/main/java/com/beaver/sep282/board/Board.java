package com.beaver.sep282.board;

import java.util.Date;

// board테이블, member테이블 join
// JavaBean(DTO) 만들때 join시킬것까지 생각해서
public class Board {
	private int b_no;
	private String b_writer;
	private Date b_when;
	private String b_text;
	private String m_photo;

	public Board() {
		// TODO Auto-generated constructor stub
	}

	public Board(int b_no, String b_writer, Date b_when, String b_text, String m_photo) {
		super();
		this.b_no = b_no;
		this.b_writer = b_writer;
		this.b_when = b_when;
		this.b_text = b_text;
		this.m_photo = m_photo;
	}

	public int getB_no() {
		return b_no;
	}

	public void setB_no(int b_no) {
		this.b_no = b_no;
	}

	public String getB_writer() {
		return b_writer;
	}

	public void setB_writer(String b_writer) {
		this.b_writer = b_writer;
	}

	public Date getB_when() {
		return b_when;
	}

	public void setB_when(Date b_when) {
		this.b_when = b_when;
	}

	public String getB_text() {
		return b_text;
	}

	public void setB_text(String b_text) {
		this.b_text = b_text;
	}

	public String getM_photo() {
		return m_photo;
	}

	public void setM_photo(String m_photo) {
		this.m_photo = m_photo;
	}

}
