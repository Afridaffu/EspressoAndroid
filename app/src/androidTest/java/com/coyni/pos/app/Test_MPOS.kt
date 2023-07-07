package com.coyni.pos.app
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
   TestGenerateQRCode::class,
   TestOnboardingAndLogin::class,
   TransactionListSearch::class,
   TestTransactionFilter::class

)
class AllTests
