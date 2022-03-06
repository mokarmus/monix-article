package org.okarmus.trasfer

import org.okarmus.transfer.TransferApp.{Account, AccountId, AccountRepository, MoneyTransferService}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable

case class InMemoryAccountRepository(
    state: mutable.Map[AccountId, Account] = mutable.Map.empty
) extends AccountRepository {

  override def findById(id: AccountId): Account = state(id)
  override def save(account: Account): Unit = state.update(account.id, account)
}

class MoneyTransferServiceSpec extends AnyWordSpec with Matchers {

  private val repository = new InMemoryAccountRepository

  private val underTest = new MoneyTransferService(repository)

  "money transfer service" should {
    "properly send money from one account to another" in {
      val account1 = Account(AccountId(1), 100)
      val account2 = Account(AccountId(2), 50)

      repository.save(account1)
      repository.save(account2)

      underTest.transfer(account1.id, account2.id, 50)

      repository.state(account1.id) == Account(AccountId(1), 50)
      repository.state(account2.id) == Account(AccountId(2), 100)

    }
  }
}
