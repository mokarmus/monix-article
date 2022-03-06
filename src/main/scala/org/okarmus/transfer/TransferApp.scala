package org.okarmus.transfer

object TransferApp {

  case class AccountId(id: Int) extends AnyVal
  case class Account(id: AccountId, balance: Double) {
    def -(amount: Double): Account = copy(balance = balance - amount)
    def +(amount: Double): Account = copy(balance = balance + amount)
  }

  trait AccountRepository {
    def findById(id: AccountId): Account
    def save(account: Account): Unit
  }

  class MoneyTransferService(repository: AccountRepository) {
    def transfer(from: AccountId, to: AccountId, amount: Double): Unit = {
      val accountFrom = repository.findById(from)
      val accountTo = repository.findById(to)

      val newAccountFrom = accountFrom - amount
      val newAccountTo = accountTo + amount

      repository.save(newAccountFrom)
      repository.save(newAccountTo)
    }
  }

}
