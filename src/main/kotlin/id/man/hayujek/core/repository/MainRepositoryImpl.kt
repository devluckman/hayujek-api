package id.man.hayujek.core.repository

import com.mongodb.client.MongoCollection
import id.man.hayujek.core.database.DatabaseComponent
import id.man.hayujek.core.database.entity.UserEntity
import id.man.hayujek.core.exception.MainException
import id.man.hayujek.core.extentions.toResult
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.setValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

/**
 *
 * Created by Lukmanul Hakim on  01/07/22
 * devs.lukman@gmail.com
 */

@Repository
class MainRepositoryImpl(
    @Autowired
    private val databaseComponent: DatabaseComponent
) : MainRepository {

    // region Initialize

    private fun hayuJekCollection(): MongoCollection<UserEntity> =
        databaseComponent.database.getDatabase("hayujek").getCollection()

    // endregion


    // region Repository User

    override fun register(userEntity: UserEntity): Result<Boolean> {
        val isExistingUser = getUserByUserName(userEntity.username)
        return if (isExistingUser.isSuccess) {
            throw MainException("User Exist!")
        } else {
            hayuJekCollection().insertOne(userEntity).wasAcknowledged().toResult()
        }
    }

    override fun updateDataUser(userEntity: UserEntity) {
        hayuJekCollection().updateOne(
            UserEntity::id eq userEntity.id,
            setValue(UserEntity::location, userEntity.location)
        )
    }

    override fun getUserById(id: String): Result<UserEntity> {
        return hayuJekCollection().findOne(UserEntity::id eq id)
            .toResult("User not found!")
    }

    override fun getUserByUserName(userName: String): Result<UserEntity> {
        return hayuJekCollection().findOne(UserEntity::username eq userName)
            .toResult("User with name $userName not found!")
    }

    // endregion
}