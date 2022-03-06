package org.okarmus.transfer

import cats.Monad
import cats.syntax.flatMap._
import cats.syntax.functor._

object TransferApp {

  case class AccountId(id: Int) extends AnyVal

  case class Account(id: AccountId, balance: Double) {
    def -(amount: Double): Account = copy(balance = balance - amount)

    def +(amount: Double): Account = copy(balance = balance + amount)
  }

  trait AccountRepository[F[_]] {
    def findById(id: AccountId): F[Account]
    def save(account: Account): F[Unit]
  }

  class MoneyTransferService[F[_]: Monad](repository: AccountRepository[F]) {
    def transfer(from: AccountId, to: AccountId, amount: Double): F[Unit] = for {
      accountFrom <- repository.findById(from)
      accountTo <- repository.findById(to)
      newAccountFrom = accountFrom - amount
      newAccountTo = accountTo + amount
      _ <- repository.save(newAccountFrom)
      _ <- repository.save(newAccountTo)
    } yield ()
  }
}
