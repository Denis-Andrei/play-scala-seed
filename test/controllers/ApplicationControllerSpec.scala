package controllers


import Repositories.DataRepository
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import models.DataModel
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.play.test.UnitSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.http.Status
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import play.api.libs.json.{JsObject, Json}
import reactivemongo.api.commands.{LastError, WriteResult}

import scala.concurrent.{ExecutionContext, Future}

class ApplicationControllerSpec extends UnitSpec with GuiceOneAppPerSuite with MockitoSugar {
  val controllerComponents: ControllerComponents = app.injector.instanceOf[ControllerComponents]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val mockDataRepository: DataRepository = mock[DataRepository]

  implicit val system: ActorSystem = ActorSystem("Sys")
  implicit val materializer: ActorMaterializer = ActorMaterializer()


  object TestApplicationController extends ApplicationController(
    controllerComponents,
    mockDataRepository,
    executionContext
  )

  val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    "test description",
    100
  )

  "ApplicationController .index()" should {

  }





  "ApplicationController .create" when {

    "the json body is valid" should {

      val jsonBody: JsObject = Json.obj(
        "_id" -> "abcd",
        "name" -> "test name",
        "description" -> "test description",
        "numSales" -> 100
      )

      val writeResult: WriteResult = LastError(ok = true, None, None, None, 0, None, updatedExisting = false, None, None, wtimeout = false, None, None)

      when(mockDataRepository.create(any()))
        .thenReturn(Future(writeResult))

      val result = TestApplicationController.create()(FakeRequest().withBody(jsonBody))

      "return CREATED" in {
        status(result) shouldBe Status.CREATED
      }
    }


    "the json body is invalid" should {

      val jsonBody: JsObject = Json.obj(
        "_id" -> 3,
        "name" -> 12,
        "description" -> "test description",
        "numSales" -> 100
      )

      val result = TestApplicationController.create()(FakeRequest().withBody(jsonBody))

      "return BAD_REQUEST" in {
        status(result) shouldBe Status.BAD_REQUEST
      }
    }
  }






  "ApplicationController .read(id: String)" should {
    when(mockDataRepository.read("_id":String))
      .thenReturn(Future(dataModel))

    val result = TestApplicationController.read("_id":String)(FakeRequest())

    "return OK" in {
      status(result) shouldBe Status.OK
    }
  }








  "ApplicationController .update(id: String)" should {
    "ApplicationController.update()" when {
      "supplied valid json" should {
        //set valid json body
        val jsonBody: JsObject = Json.obj(
          "_id" -> "abcd",
          "name" -> "test name",
          "description" -> "test description",
          "numSales" -> 100
        )

        //setup result

        "return a ACCEPTED" in {

          when(mockDataRepository.update(dataModel))
            .thenReturn(Future(dataModel))
          val result = TestApplicationController.update("_id": String)(FakeRequest().withBody(jsonBody)) //fails when before when
          //check http status code of result

          status(result) shouldBe Status.ACCEPTED
        }
        "return the correct JSON body" in {
          val result = TestApplicationController.update("_id": String)(FakeRequest().withBody(jsonBody))
          //check json body of result
          await(jsonBodyOf(result)) shouldBe jsonBody
        }
      }
      "supplied invalid json" should {
        //set invalid json body
        val jsonBody: JsObject = Json.obj(
          "_id" -> true,
          "name" -> 12,
          "description" -> "test description",
          "numSales" -> 100
        )
        //setup result
        val result = TestApplicationController.update("_id": String)(FakeRequest().withBody(jsonBody))
        "return a BAD_REQUEST" in {
          //check http status code
          status(result) shouldBe Status.BAD_REQUEST
        }
      }
    }
  }









  "ApplicationController .delete(id: String)" should {
    val writeResult: WriteResult = LastError(ok = true, None, None, None, 0, None, updatedExisting = false, None, None, wtimeout = false, None, None)

    when(mockDataRepository.delete("_id":String))
      .thenReturn(Future(writeResult))

    val result = TestApplicationController.delete("_id":String)(FakeRequest())

    "return ACCEPTED" in {
      status(result) shouldBe Status.ACCEPTED
    }
  }







  "ApplicationController .index" should {

    when(mockDataRepository.find(any())(any()))
      .thenReturn(Future(List(dataModel)))

    val result = TestApplicationController.index()(FakeRequest())

    "return OK" in {
      status(result) shouldBe Status.OK
    }
  }


}
