package org.oddjob.userguide;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MockExecutorService implements ExecutorService {

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
			throws InterruptedException {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public <T> List<Future<T>> invokeAll(
			Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
			throws InterruptedException, ExecutionException {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
			long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public boolean isShutdown() {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public boolean isTerminated() {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public void shutdown() {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public List<Runnable> shutdownNow() {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public Future<?> submit(Runnable task) {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public void execute(Runnable command) {
		throw new RuntimeException("Unexpected from " + getClass());
	}
}
