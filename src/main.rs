mod handler;
mod functions;

pub mod pb {
    include!("gen/mod.rs");
}

use tonic::transport::Server;
use pb::delay::v1::light_delay_server::LightDelayServer;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let addr = "[::1]:50051".parse().unwrap();
    let service = LightDelayServer::new(handler::grpc::LightDelayImpl::default());

    println!("server listening on {}", addr);

    Server::builder()
        .add_service(service)
        .serve(addr)
        .await?;

    Ok(())
}