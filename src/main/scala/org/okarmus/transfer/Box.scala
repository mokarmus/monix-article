package org.okarmus.transfer

import monix.eval.Task

trait Box[F[_]] {
  def unit[A](a: => A): F[A]
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
  def map[A, B](fa: F[A])(f: A => B): F[B] = flatMap(fa)(a => unit(f(a)))
}

object Box {
  type Id[X] = X

  implicit val taskBox: Box[Task] = new Box[Task] {
    override def unit[A](a: => A): Task[A] = Task.eval(a)
    override def flatMap[A, B](fa: Task[A])(f: A => Task[B]): Task[B] = fa.flatMap(f)
  }

  implicit val idBox: Box[Id] = new Box[Id] {
    override def unit[A](a: => A): A = a
    override def flatMap[A, B](fa: A)(f: A => B): B = f(fa)
  }

  implicit class BoxOps[F[_], A](fa: F[A])(implicit m: Box[F]) {
    def flatMap[B](f: A => F[B]): F[B] = m.flatMap(fa)(f)
    def map[B](f: A => B): F[B] = m.map(fa)(f)
  }
}
