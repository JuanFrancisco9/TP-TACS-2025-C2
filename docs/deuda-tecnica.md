# Posibles mejoras 

Este documento enumera mejoras a considerar para la próxima Entrega.

- **Evento**
    - Cuando el organizador quiere ver sus eventos, debería tener la posibilidad de ver si todavía un evento en particular no alcanzó el cupo mínimo (cuando haya). Se tiene un estado `Pendiente` para el evento que implica un flujo distinto en las inscripciones. Faltaría contemplar que puedas inscribirte a un evento "Pendiente" pero que se te avise por medio de la response.

- **Repositorios**
  - Si bien se utiliza el Patrón Repositorio para simular la persistencia de datos, habrá un refactor importante cuando se tenga persistencia en la BD no relacional. 

  - **Integración Front-Back: Registro de usuarios**
    - Actualmente el endpoint para registrarse espera un id para completar el registro, pero desde el front esto no es posible. Cuando se tenga persistencia en NoSQL hay que eliminar este atributo y devolver un id desde la BD (UUID?).
    - Cambiar checkbox de precio por rango de precios.
    - Faltan algunos filtros en los eventos referidos al rango de precios y de fechas. 

  - **Imágenes**
    - Sumar las imagenes para devolverlas desde el back, también los íconos de las categorías.
  

