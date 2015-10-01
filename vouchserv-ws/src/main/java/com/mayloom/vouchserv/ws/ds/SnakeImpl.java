package com.mayloom.vouchserv.ws.ds;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.google.common.base.Optional;

public class SnakeImpl<E> implements Snake<E> {
	
	Deque<E> queue = new ArrayDeque<E>();
	
	public boolean eat(E e) {
		return queue.add(e);
	}

	public Optional<E> checkTail() {
		E element = queue.peek();
		if (element == null) {
			return Optional.absent();
		} else {
			return Optional.of(element);
		}
	}

	public Optional<E> excrete() {	
		E element = queue.poll();
		if (element == null) {
			return Optional.absent();
		} else {
			return Optional.of(element);
		}
	}

	public int size() {
		return queue.size();
	}

	public Iterator<E> iterator() {
		return queue.iterator();
	}

	public Optional<E> checkHead() {
		E element = queue.peekLast();
		if (element == null) {
			return Optional.absent();
		} else {
			return Optional.of(element);
		}
	}

}
