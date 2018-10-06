import com.google.inject.Inject
import com.ing.roomregistry.filters.LoggingFilter
import play.api.http.DefaultHttpFilters
import play.api.http.EnabledFilters
import play.filters.gzip.GzipFilter

class Filters @Inject() (defaultFilters: EnabledFilters,
                          gzip: GzipFilter,
                          log: LoggingFilter
                        ) extends DefaultHttpFilters(defaultFilters.filters :+ gzip :+ log: _*)
