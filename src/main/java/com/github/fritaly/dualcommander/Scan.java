package com.github.fritaly.dualcommander;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public final class Scan implements FileVisitor<Path> {

	private int files, directories;

	private long totalSize;

	public Scan() {
	}

	public int getFiles() {
		return files;
	}

	public int getDirectories() {
		return directories;
	}

	public long getTotalSize() {
		return totalSize;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		directories++;

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		files++;
		totalSize += file.toFile().length();

		return FileVisitResult.CONTINUE;
	}

	public void visitFile(File file) {
		files++;
		totalSize += file.length();
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		return FileVisitResult.TERMINATE;
	}
}