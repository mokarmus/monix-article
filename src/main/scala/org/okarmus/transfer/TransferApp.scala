package org.okarmus.transfer

import monix.eval.Task

object TransferApp {

  case class AccountId(id: Int) extends AnyVal

  case class Account(id: AccountId, balance: Double) {
    def -(amount: Double): Account = copy(balance = balance - amount)

    def +(amount: Double): Account = copy(balance = balance + amount)
  }

  trait AccountRepository {
    def findById(id: AccountId): Task[Account]
    def save(account: Account): Task[Unit]
  }

  class MoneyTransferService(repository: AccountRepository) {
    def transfer(from: AccountId, to: AccountId, amount: Double): Task[Unit] = for {
      accountFrom <- repository.findById(from)
      accountTo <- repository.findById(to)
      newAccountFrom = accountFrom - amount
      newAccountTo = accountTo + amount
      _ <- repository.save(newAccountFrom)
      _ <- repository.save(newAccountTo)
    } yield ()
  }
}
