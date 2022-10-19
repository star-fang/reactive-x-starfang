package com.rx.starfang

import android.app.Application
import com.rx.starfang.database.room.StarfangRoomDatabase
import com.rx.starfang.database.room.memo.MemoRepository
import com.rx.starfang.database.room.rok.RokRepository
import com.rx.starfang.database.room.talk.TalkRepository
import com.rx.starfang.database.room.terminal.TerminalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RxStarfangApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())
    private val roomDatabase by lazy { StarfangRoomDatabase.getDatabase(this, applicationScope)}
    val memoRepository by lazy { MemoRepository(roomDatabase.memoDao())}
    val talkRepository by lazy { TalkRepository(roomDatabase.talkDao())}
    val terminalRepository by lazy { TerminalRepository(roomDatabase.lineDao()) }
    val rokRepository by lazy { RokRepository(roomDatabase.rokDaoMap(), roomDatabase.rokSearchDao()) }

    /*
    override fun onCreate() {
        super.onCreate()


        val realmConfig = RealmConfiguration
            .Builder(schema = setOf(Attribute::class, Civilization::class, Commander::class, Gain::class, LanguagePack::class, Rarity::class, Skill::class, Specification::class, Unit::class, UnitSpec::class, TestModel::class))
            .name("rx.starfang.realm")
            .migration(AutomaticSchemaMigration {
                context: AutomaticSchemaMigration.MigrationContext ->
                val newRealm: DynamicMutableRealm = context.newRealm

                context.enumerate("Commander") {
                        oldObject: DynamicRealmObject, newObject: DynamicMutableRealmObject? ->
                    newObject?.run {
                        // Merge property
                        //set( "fullName", "${oldObject.getValue<String>("firstName")} ${ oldObject.getValue<String>("lastName") }" )

                        // Rename property
                        //set("renamedProperty", oldObject.getValue<String>("property"))

                        // Change type
                        set("name", oldObject.getValue<String>("name") to LanguagePack::class)
                    }
                }
            }).build()
        realm = Realm.open(realmConfig)

    }
     */


}