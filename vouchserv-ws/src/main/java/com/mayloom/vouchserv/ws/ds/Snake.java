package com.mayloom.vouchserv.ws.ds;

import java.util.Iterator;

import com.google.common.base.Optional;

/**
* @author Nico de Wet
* @param <E> the type of elements held in this collection
*/
public interface Snake<E> {

	/**
	 * Insert a new element into the head of the snake.
	 * 
	 * @param e
	 * @return
	 */
    boolean eat(E e);
    
    /**
     * Retrieve a copy of the most recently eaten item, at the head end of the snake.
     * 
     * @return
     */
    Optional<E> checkHead();
    
    /**
     * Retrieve a copy of the oldest item, at the tail end of the snake.
     * 
     * @return
     */
    Optional<E> checkTail();
   
    /**
     * Remove the oldest item from the snake, at its tail end.
     * 
     * @return
     */
    Optional<E> excrete();
    
    /**
     * The number of elements in the snake.
     * 
     * @return
     */
    int size();
    
    /**
     * Iterate from tail of snake (oldest item) to head (newest item)
     * 
     * @return
     */
    Iterator<E> iterator();
   
}

