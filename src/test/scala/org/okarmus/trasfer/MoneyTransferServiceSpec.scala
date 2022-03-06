package org.okarmus.trasfer

import monix.eval.Task
import monix.execution.Scheduler
import org.okarmus.transfer.TransferApp.{Account, AccountId, AccountRepository, MoneyTransferService}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.collection.mutable

case class InMemoryAccountRepository(
    state: mutable.Map[AccountId, Account] = mutable.Map.empty
) extends AccountRepository {

  override def findById(id: AccountId): Task[Account] = Task { state(id) }
  override def save(account: Account): Task[Unit] = Task { state.update(account.id, account) }
}

class MoneyTransferServiceSpec extends AsyncWordSpec with Matchers {

  implicit val scheduler: Scheduler = Scheduler.io()

  private val repository = new InMemoryAccountRepository

  private val underTest = new MoneyTransferService(repository)

  "money transfer service" should {
    "properly send money from one account to another" in {
      val account1 = Account(AccountId(1), 100)
      val account2 = Account(AccountId(2), 50)

      (for {
        _ <- repository.save(account1)
        _ <- repository.save(account2)
        _ <- underTest.transfer(account1.id, account2.id, 50)
      } yield ())
        .map(_ =>
          (repository.state(account1.id), repository.state(account1.id)) shouldBe
            (Account(AccountId(1), 50), Account(AccountId(2), 100))
        )
        .runToFuture
    }
  }
}
