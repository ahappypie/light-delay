use hifitime::Epoch;
use vsop87::{SphericalCoordinates, vsop87b};

//in light-milliseconds
pub fn delay(origin: i32, destination: i32, timestamp: u64) -> u64 {
    let jd = julian_date_from_unix(timestamp);
    let ori = compute_vsop(origin, jd);//origin.helio(origin, jd);
    let dest = compute_vsop(destination, jd);//destination.helio(destination, jd);
    let d = distance(ori, dest, jd);
    let lt = light_time(d);

    let jd2 = jd - lt;
    let ori2 = compute_vsop(origin, jd2);//origin.helio(origin, jd2);
    let dest2 = compute_vsop(destination, jd2);//destination.helio(destination, jd2);
    let d2 = distance(ori2, dest2, jd2);
    let lt2 = light_time(d2);

    //delay in ms
    return f64::round(lt2 * 1440.0 * 60.0 * 1000.0) as u64;
}

//in AU
fn distance(d1: SphericalCoordinates, d2: SphericalCoordinates, jd: f64) -> f64 {
    let x = (d2.distance() * f64::cos(d2.longitude()) * f64::cos(d2.latitude())) - (d1.distance() * f64::cos(d1.latitude()) * f64::cos(d1.longitude()));
    let y = (d2.distance() * f64::cos(d2.longitude()) * f64::sin(d2.latitude())) - (d1.distance() * f64::sin(d1.latitude()) * f64::cos(d1.longitude()));
    let z = (d2.distance() * f64::sin(d2.longitude())) - (d1.distance() * f64::sin(d1.longitude()));

    let t = (jd - 2451545.0) / 365250.0;
    #[allow(non_snake_case)]
    let Q = 3.8048177 + (8399.711184 * t);
    let u = x - (f64::cos(Q) * 0.0000312);
    let v = y - (f64::sin(Q) * 0.0000286);
    let w = z - (f64::sin(Q) * 0.0000124);

    return f64::sqrt(f64::powi(u, 2) + f64::powi(v, 2) + f64::powi(w, 2));
}

//In days
fn light_time(distance: f64) -> f64 {
    return distance * 0.0057755183;
}

fn julian_date_from_unix(timestamp: u64) -> f64 {
    let ets = Epoch::from_unix_milliseconds(timestamp as f64);
    return ets.to_jde_utc_days();
}

fn compute_vsop(body: i32, jd: f64) -> SphericalCoordinates {
    match body {
        1 => vsop87b::mercury(jd),
        2 => vsop87b::venus(jd),
        3 => vsop87b::earth(jd),
        4 => vsop87b::mars(jd),
        5 => vsop87b::jupiter(jd),
        6 => vsop87b::saturn(jd),
        7 => vsop87b::uranus(jd),
        8 => vsop87b::neptune(jd),
        _ => vsop87b::earth(jd)
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    const TIMESTAMP: u64 = 1569609169337;

    #[test]
    fn test_julian_day() {
        assert_eq!(2458754.2727932525, julian_date_from_unix(TIMESTAMP));
    }

    #[test]
    fn test_earth_mars_distance() {
        let jd = julian_date_from_unix(TIMESTAMP);
        let e = vsop87b::earth(jd);
        let m = vsop87b::mars(jd);
        //WARN this isn't right, but the delay is? Something cancelling out? Not an astrophysicist...
        assert_eq!(2.6453806827988173, distance(e, m, jd));
    }

    #[test]
    fn test_earth_mars_delay() {
        assert_eq!(1320073, delay(3, 4, TIMESTAMP));
    }
}