$root = Join-Path $PSScriptRoot "..\src"
Get-ChildItem -Path $root -Recurse -Filter *.java | ForEach-Object {
    $p = $_.FullName
    $c = [System.IO.File]::ReadAllText($p)
    $o = $c
    $c = $c.Replace('import com.skaly.fashion_backend.user.UserEntity;', 'import com.skaly.fashion_backend.user.infrastructure.persistence.entities.UserEntity;')
    $c = $c.Replace('import com.skaly.fashion_backend.user.User;', 'import com.skaly.fashion_backend.user.domain.entities.User;')
    $c = $c.Replace('com.skaly.fashion_backend.order.OrderStatusHistoryEntity', 'com.skaly.fashion_backend.order.infrastructure.persistence.entities.OrderStatusHistoryEntity')
    $c = $c.Replace('com.skaly.fashion_backend.order.OrderNoteEntity', 'com.skaly.fashion_backend.order.infrastructure.persistence.entities.OrderNoteEntity')
    $c = $c.Replace('com.skaly.fashion_backend.order.OrderItemEntity', 'com.skaly.fashion_backend.order.infrastructure.persistence.entities.OrderItemEntity')
    $c = $c.Replace('com.skaly.fashion_backend.order.ShippingEntity', 'com.skaly.fashion_backend.order.infrastructure.persistence.entities.ShippingEntity')
    $c = $c.Replace('com.skaly.fashion_backend.order.OrderEntity', 'com.skaly.fashion_backend.order.infrastructure.persistence.entities.OrderEntity')
    $c = $c.Replace('import com.skaly.fashion_backend.order.OrderItem;', 'import com.skaly.fashion_backend.order.domain.entities.OrderItem;')
    $c = $c.Replace('import com.skaly.fashion_backend.order.Order;', 'import com.skaly.fashion_backend.order.domain.entities.Order;')
    $c = $c.Replace('com.skaly.fashion_backend.cart.CartItemEntity', 'com.skaly.fashion_backend.cart.infrastructure.persistence.entities.CartItemEntity')
    $c = $c.Replace('com.skaly.fashion_backend.cart.CartEntity', 'com.skaly.fashion_backend.cart.infrastructure.persistence.entities.CartEntity')
    $c = $c.Replace('import com.skaly.fashion_backend.cart.CartItem;', 'import com.skaly.fashion_backend.cart.domain.entities.CartItem;')
    $c = $c.Replace('import com.skaly.fashion_backend.cart.Cart;', 'import com.skaly.fashion_backend.cart.domain.entities.Cart;')
    if ($c -ne $o) {
        [System.IO.File]::WriteAllText($p, $c)
        Write-Host "Updated: $p"
    }
}
