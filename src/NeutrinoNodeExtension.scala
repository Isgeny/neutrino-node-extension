import com.wavesplatform.account.{Address, PublicKey}
import com.wavesplatform.block.{Block, MicroBlock}
import com.wavesplatform.common.state.ByteStr
import com.wavesplatform.common.utils.EitherExt2
import com.wavesplatform.events.BlockchainUpdateTriggers
import com.wavesplatform.extensions.{Context, Extension}
import com.wavesplatform.lang.v1.FunctionHeader
import com.wavesplatform.lang.v1.compiler.Terms
import com.wavesplatform.lang.v1.compiler.Terms.FUNCTION_CALL
import com.wavesplatform.state.Blockchain
import com.wavesplatform.state.diffs.BlockDiffer
import com.wavesplatform.transaction.smart.InvokeScriptTransaction
import com.wavesplatform.transaction.smart.InvokeScriptTransaction.Payment
import com.wavesplatform.transaction.{Asset, Proofs, TxPositiveAmount, TxVersion}
import com.wavesplatform.utils.ScorexLogging

import scala.collection.immutable.HashSet
import scala.concurrent.Future

class NeutrinoNodeExtension(private val context: Context) extends Extension
    with ScorexLogging
    with BlockchainUpdateTriggers {

  // -----------INPUT PARAMETERS----------

  // Beneficiary address for rewards 3P...
  private val beneficiary = ""

  // Offset from current timestamp back to past (ms)
  private val timestampOffset = 0

  // Amount of transactions to send when neutrino node's block appeared
  private val transactionsAmount = 0

  // Timestamp delta between transactions (ms)
  private val transactionsDelta = 0

  // -------------------------------------

  private val chainId = 'W'.toByte
  private val registryAddress = Address.fromString("3P9vKqQKjUdmpXAfiWau8krREYAY1Xr69pE").explicitGet()
  private val version = TxVersion.V2
  private val feeAssetId = Asset.Waves
  private val fee = TxPositiveAmount.unsafeFrom(500000)
  private val payments = scala.Seq(Payment(2400000000L, feeAssetId))
  private val proofs = Proofs.empty
  private val functionCall = Option(
    FUNCTION_CALL(FunctionHeader.User("distributeMinerReward"),
    List(Terms.CONST_STRING(beneficiary).explicitGet()))
  )

  private val nodes = HashSet(
    PublicKey.fromBase58String("4rRr8fWrf6uVxehZDP9uHfLtags7w4nEoUX5bcB1sWEs").explicitGet().base64,
    PublicKey.fromBase58String("Ea2hjE3n8vAejnhH9x2ooDpQqqzSBe4uGUoiMfAvkX9U").explicitGet().base64,
    PublicKey.fromBase58String("9c2WH27MzjYv4ZxxmWLf3NF5Uy2yDap82MjM7Xz2Bppn").explicitGet().base64,
    PublicKey.fromBase58String("8FkoiH9rMRB7CX1HJpP4ihi2StUUeYbwMaWizQ1KnryD").explicitGet().base64,
    PublicKey.fromBase58String("FT7ZdYF9odiTCybJ7m8wT2mLb5QoYKaGjHSc1ZjivD7Y").explicitGet().base64,
    PublicKey.fromBase58String("3yAWt4gt46AoTgmW7V8uAXzKgGipABUgNBFkaSFuCC9p").explicitGet().base64,
    PublicKey.fromBase58String("CQJnFZ4Bd7SmmqdzxFZmg5WmQcaQGbKUdnjKuHX7XAEZ").explicitGet().base64,
    PublicKey.fromBase58String("2omSAmiDGjgZjdiDiTvGqarqB88T89LZBpzcXgBgKarh").explicitGet().base64,
    PublicKey.fromBase58String("CPyACYcHkZvR6BgWynaF1fBwooyQn2LV8YNPhSFMQ4Tq").explicitGet().base64,
    PublicKey.fromBase58String("Bw7VouakkYA3FfTefsXPE3x2UK7zriiyzcybE9LfS88m").explicitGet().base64,
    PublicKey.fromBase58String("3z5mdd47uWE3ss3ru1FYCeU4t9wsPkCWLcmpBkTZS5um").explicitGet().base64,
    PublicKey.fromBase58String("A4v4Lw7RkoBEXMWu8i58iuMyQga1sdXT95f4gfyKuuaG").explicitGet().base64,
    PublicKey.fromBase58String("frBcbd9Viv2Wohnw5VnxZx67B2DNu8dNdMdA23DzjRq").explicitGet().base64,
    PublicKey.fromBase58String("2Memb8Qy7AaQGHvEK9XEh8Y69ftSg8Jzbb4mgTjPdYjm").explicitGet().base64,
    PublicKey.fromBase58String("EgrApxC45Pnb1Uosc6HaYiwivNbAeKMMY2mWX29wthU3").explicitGet().base64,
    PublicKey.fromBase58String("DATSoes21KodfPGityg3d4NSeb5iHJEFGr7z3q85SPXG").explicitGet().base64,
    PublicKey.fromBase58String("81gC4CDsyLKV3dZoum3xq6jsHdxFAiqi1bKdTpkBcJFc").explicitGet().base64,
    PublicKey.fromBase58String("87GZPxLsKpWSvgYAKSxcE8xPMfud6bRUDBVaWBKKqYNs").explicitGet().base64,
    PublicKey.fromBase58String("3sToq3KPa8AhFP5SbNkaMGLMjvgoimP85dFqMFNZWpRh").explicitGet().base64,
    PublicKey.fromBase58String("274RhVaraPzW5HwEY16iizfe8fhYFKU7eikC3DcU3oDm").explicitGet().base64,
    PublicKey.fromBase58String("68ucmXotVWvBZwzDWcRgeab9LvGUPPyvP7vL7xXb9w4w").explicitGet().base64,
    PublicKey.fromBase58String("7xyCnhiDtuv1mmYbY3feqgUeq2iBT75kZk4o5uFe5r4T").explicitGet().base64,
    PublicKey.fromBase58String("A4MPJQmbo9dEVCvdxounAnz6tDVrRjkVQj7zCtzbeAw2").explicitGet().base64,
    PublicKey.fromBase58String("9Znsh3cvxEHrkLqnGRUPPbEn5yXFCaEtdoQ6r6UfhMgj").explicitGet().base64,
    PublicKey.fromBase58String("8vGHYH7aJEUMraDGVdkQDgreQfxcsQm5mBtRp1ZH4oc3").explicitGet().base64,
    PublicKey.fromBase58String("73JVj1ftyUrkRNUk78iGcQ4iXno2NTPjGZLxEfbv6VyV").explicitGet().base64,
    PublicKey.fromBase58String("EkQBLHMPs4pv7J2zgzXmorgvL5qo141RzJAYFecXCWhm").explicitGet().base64,
    PublicKey.fromBase58String("EUcGJjoT6sv1vK5QAyiwJFGpUmC7CNKNmKGGX46U3nPG").explicitGet().base64,
    PublicKey.fromBase58String("GoXyjtZmjW2TvYeJ4LPfiYiKaPtiqGAWe4dHrZ5asVZ9").explicitGet().base64,
    PublicKey.fromBase58String("AbPQimoVReDWznbXkDjNcJB9WNQHYeHWyqXMG41dRGJm").explicitGet().base64,
    PublicKey.fromBase58String("6akSzCX2jUFm2iPUc1z6g29Dsddaxi1W3SVtMesT6XA8").explicitGet().base64,
    PublicKey.fromBase58String("9FmmncTYzTqLf2m89tbEHnmm9TdiMgkNWwV4THWa7NKU").explicitGet().base64,
    PublicKey.fromBase58String("CkdaASBbKSEGqyFY4x1gStcVY5je3UkxShQYgk8HehEK").explicitGet().base64,
    PublicKey.fromBase58String("D2v9QevzsPnwBbqSrjzFCLJTcRxYne4rcShhAjGQvoW6").explicitGet().base64,
    PublicKey.fromBase58String("6e3wc76ZAEi7r1XQTspw8sPkW4DzZkwSSy7rGGZsCyYy").explicitGet().base64,
    PublicKey.fromBase58String("9x5gfVKoQ838yNFVYSHMHVh7PSG4Hb7pZoacUPNetvL8").explicitGet().base64,
    PublicKey.fromBase58String("6GjJ43uZcUemU24qmMr9iBUjf7YgWGAwrkSpUcUXRUfg").explicitGet().base64,
    PublicKey.fromBase58String("AAwq4uuEs79KAiQmvN4jufJWgh2git8wkQAMoDjHqqT9").explicitGet().base64,
    PublicKey.fromBase58String("BDWs6iVsBR5ZFHNCHrrAMJ5xjCjmdN7xG99oRESWozGW").explicitGet().base64,
    PublicKey.fromBase58String("32zjWCnjTYApoN9XbcyEvoQMjF5awbpmd3JqZmtDSsyk").explicitGet().base64,
    PublicKey.fromBase58String("26ByXdmeXW6QZ9qQq25SxhZ46KaLDiRhTGc2YCRnicqr").explicitGet().base64,
    PublicKey.fromBase58String("Bkvx8tf7LiCQ5Yq2dGCUH9TbbxuheD2EJ4zHsxdB3JLq").explicitGet().base64,
    PublicKey.fromBase58String("6kaGbu36tX3gM97iVuQ5ZaqM1vKcXsZqihzbKrtKT4Lh").explicitGet().base64,
    PublicKey.fromBase58String("Af7xvxhMTp3q1FkgUeywA6g7nSudQ9ZixtfFWcp56f72").explicitGet().base64,
    PublicKey.fromBase58String("6d8V4wW8sA5vy6S9o8UgqvdXxtt1iPyHBFgDNVMeokg4").explicitGet().base64,
    PublicKey.fromBase58String("CNgCh5uE53P9DbtR89orRzC6Vi6zEsPFpAq9Xqhax8Vh").explicitGet().base64,
    PublicKey.fromBase58String("HJkercTczUZ1H7U4V4Y18KQBbFfumGV9x3mPsWukTPoH").explicitGet().base64,
    PublicKey.fromBase58String("JTs3x2DxPSKq9ityrLPcVjGGy9a1eB5wuYDjqyUEw9P").explicitGet().base64,
    PublicKey.fromBase58String("2mV5dg8bGcJ7g3NTEZzBMdoZTqRgGQuyRnsuHMxsykYQ").explicitGet().base64,
    PublicKey.fromBase58String("72VeGLtjQENWbHQhJfTWTbXGma8Fd4Rs9hnoJ2icTHw").explicitGet().base64,
    PublicKey.fromBase58String("6uc4pibY35qrf1rmbfhSxwCae1yicuYJStRt6ArEL6Fx").explicitGet().base64,
    PublicKey.fromBase58String("5G58z3bsDDEuYb5b5h3cJmRVjA4Jmr589FTePY41SLr1").explicitGet().base64,
    PublicKey.fromBase58String("4K6FBhN7Np6yNPqRmMMSzbEJJE31KuWQk4Ly2wu7QzGc").explicitGet().base64,
    PublicKey.fromBase58String("3XXesaFomL7MzSHmmXpjrF5JCBi4V4nz3kEY7AjKrb2m").explicitGet().base64,
    PublicKey.fromBase58String("26R1xPoDTnPauhNpu8GTzzDe5dvCjwGsFQBuxvX9FhQX").explicitGet().base64,
    PublicKey.fromBase58String("FPUBwJKPMe6pzX2pXazGzTpFhgF5BbP5XEPgJuBPbzRW").explicitGet().base64,
    PublicKey.fromBase58String("CvJ7r3raeYDdTwFqfMRjwFCnbooprMLLEhPXnsuvbV2K").explicitGet().base64,
    PublicKey.fromBase58String("EBmcV2LgYgUjo7Ak4w9wckabCZuPjxT8bt2pm8Lnj2US").explicitGet().base64,
    PublicKey.fromBase58String("C3pGJo9WHaXh6izgePAJe7KkKJaJ6yZg78vuaxmr7fXg").explicitGet().base64,
    PublicKey.fromBase58String("6WrECcwLxbiDBtNbtqeUtkKsK5FHonC4SDHZS8Z72Arb").explicitGet().base64,
    PublicKey.fromBase58String("GY2VPGvY6Mzi4MiGFvsBdcY4TGYy1khodqrY5D3ZSjU5").explicitGet().base64,
    PublicKey.fromBase58String("3Qf8H4W5vanKr4h2bA3LiEt9ski9eB4ioprRXppL52V9").explicitGet().base64,
    PublicKey.fromBase58String("BQPobsDAa9Pqn9Uaw7B3ihdqATUTxm1JD5t18eqVY1HL").explicitGet().base64,
    PublicKey.fromBase58String("FFpDw51CnGrBuxfwPAvRb9HtfTXesFLKq7JgZyJsoVYc").explicitGet().base64,
    PublicKey.fromBase58String("5zr5rbLdUg1zxZjTxdXeK8UWBk51ifF8ZfkcRN26z2bT").explicitGet().base64,
    PublicKey.fromBase58String("2Xh3pZ8ByXbkbDZ7APNB6nttYkTQZ67HvFsJ6c2hXSyj").explicitGet().base64,
    PublicKey.fromBase58String("5RVYhNpfV5RDTbhRBqdSJ7k5YebC4QptZKSwap47Wrid").explicitGet().base64,
    PublicKey.fromBase58String("GEAzSi9QYFemZfkyCi62XdqaYrYi6fdCYuqMVVjdEz95").explicitGet().base64,
    PublicKey.fromBase58String("9y2ysnnJgWBvMZL4STP9d1itDMck6DJm4FXXZ2DNJNyF").explicitGet().base64,
    PublicKey.fromBase58String("FunyrXAmhBnFUmVTVTR4EsaUpct5B2Qpe3QyEzTBriia").explicitGet().base64,
    PublicKey.fromBase58String("EDzmotJuLWJ6im48nTjDm1qmKApBrfJEQmwovfqE9YW8").explicitGet().base64,
    PublicKey.fromBase58String("4UhqfgxWaHLuhW3DKiReWjvtdVbdFRyRjUZuHPtnNCFB").explicitGet().base64,
    PublicKey.fromBase58String("973rEw9gpiU728nS9a3nAjdgMrEEG7vzLhef7D6sFhnu").explicitGet().base64,
    PublicKey.fromBase58String("BTU3a9khTfsZhT6vJ2o4KrwF9XXvaJv8waBWkcfhaimH").explicitGet().base64,
    PublicKey.fromBase58String("GZvPG5YPApFC1a2ngrXhyWfoERV3o58ZJ7qY2WqKXNRi").explicitGet().base64,
    PublicKey.fromBase58String("HCieAZHu5NgSjX6LwjQohpyatN7vv3rfTfKGTAdFqStt").explicitGet().base64,
    PublicKey.fromBase58String("7Ley8b6fVF4TLn1DMTv8xaaPLMP1Yf6oiHczPehLHR4u").explicitGet().base64,
    PublicKey.fromBase58String("7mNBmEFMUm4nFk8WXMb4sRDTpvX2q3oDU9SSEfAK2PZf").explicitGet().base64,
    PublicKey.fromBase58String("D2p7qd2VpCP1ESqJL32UnqnsSz647AiPJNmrW83NtZKY").explicitGet().base64,
    PublicKey.fromBase58String("FiQB7AGJLTi1Hn6eVx1GFcVhkYJ7aAurQ1TraLt1uxrg").explicitGet().base64,
  )

  override def onProcessBlock(block: Block, diff: BlockDiffer.DetailedDiff, minerReward: Option[Long], blockchainBeforeWithMinerReward: Blockchain): Unit = {
    if (nodes.contains(block.sender.base64)) {
      var ts = System.currentTimeMillis() - timestampOffset
      for (t <- 1 to transactionsAmount) {
        ts += t * transactionsDelta
        val transaction = InvokeScriptTransaction(version, block.sender, registryAddress, functionCall, payments, fee, feeAssetId, ts, proofs, chainId)
        context.transactionsApi.broadcastTransaction(transaction)
      }
    }
  }

  override def start(): Unit = {}

  override def shutdown(): Future[Unit] = Future.unit

  override def onProcessMicroBlock(microBlock: MicroBlock, diff: BlockDiffer.DetailedDiff, blockchainBeforeWithMinerReward: Blockchain, totalBlockId: ByteStr, totalTransactionsRoot: ByteStr): Unit = {}

  override def onRollback(blockchainBefore: Blockchain, toBlockId: ByteStr, toHeight: Int): Unit = {}

  override def onMicroBlockRollback(blockchainBefore: Blockchain, toBlockId: ByteStr): Unit = {}
}
